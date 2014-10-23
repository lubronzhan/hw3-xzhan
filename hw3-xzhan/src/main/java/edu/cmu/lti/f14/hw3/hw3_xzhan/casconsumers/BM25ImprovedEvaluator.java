package edu.cmu.lti.f14.hw3.hw3_xzhan.casconsumers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import edu.cmu.lti.f14.hw3.hw3_xzhan.typesystems.Document;
import edu.cmu.lti.f14.hw3.hw3_xzhan.typesystems.Token;
import edu.cmu.lti.f14.hw3.hw3_xzhan.utils.Utils;


public class BM25ImprovedEvaluator extends CasConsumer_ImplBase {

	/** whole doc of different id 
	 *
	 * map<id, list<Term>>
	 *
	 **/
	public Map<Integer, ArrayList<Line>> docData;
	
	/** doc of each id doc.
	 *  doc in arraylist 
	 *  list<token, frequency>
	 */
	public ArrayList<Line> eachD;
	
	/** query of each id 
	 * 
	 * query in map  <queryId, <token, frequency>>
	 **/
	public Map<Integer, Map<String,Integer>> eachQ;
	
	/** store the last id **/
	public int lastId;

  /** rank of each id **/
	public Map<Integer, Integer> idRank;
	
	
	// for addtion bm
	
	/** query frequency. word frequency in query **/
//	public Map<Integer, Map<String, Integer>> qf;
	public double qf;
	

	

	

	
	
	
	
		
	public void initialize() throws ResourceInitializationException {

		docData = new HashMap<Integer, ArrayList<Line>>();
		
		eachD = new ArrayList<Line>();
		
		eachQ = new HashMap<Integer, Map<String,Integer>>();
		
		idRank = new HashMap<Integer, Integer>();
		
//		qf = new HashMap<Integer, Map<String,Integer>>();
		qf = 0;
		

		
		lastId = 1;
		
	
		
		
	}

	/**
	 * TODO :: 1. construct the global word dictionary 2. keep the word
	 * frequency for each sentence
	 * 
	 * Store each line's info into Term instance. Then store all the Term into docData map.
	 * 
	 * 
	 */
	@Override
	public void processCas(CAS aCas) throws ResourceProcessException {

		JCas jcas;
		try {
			jcas =aCas.getJCas();
		} catch (CASException e) {
			throw new ResourceProcessException(e);
		}

		FSIterator it = jcas.getAnnotationIndex(Document.type).iterator();
		


		if (it.hasNext()) {
			Document doc = (Document) it.next();

			//Make sure that your previous annotators have populated this in CAS
			FSList fsTokenList = doc.getTokenList();
			
			ArrayList<Token> tokenList =Utils.fromFSListToCollection(fsTokenList, Token.class);
		
			//Do something useful here

			Line line = new Line();
			
			// id
			int queryId;
			queryId = doc.getQueryID();
			
			if(lastId != queryId){// another query id 
			  // add each in former trunk doc to data with last id
			  docData.put(lastId, eachD); 
			  
	
			  
			  // new ArrayList to store doc with new id. 
			  eachD = new ArrayList<Line>();
			  

			  
//			  eachD.clear();
			  // update id.
			  lastId = queryId;
	  
			}
			
			int length = 0;
			
			// if is query
  		if(doc.getRelevanceValue() == 99){
  		  Map<String, Integer> queryVector = new HashMap<String, Integer>();
  		  for(Token k : tokenList){
  	      queryVector.put(k.getText(), k.getFrequency());
  		  }
  		  
  		  // add to query map
  		  eachQ.put(queryId, queryVector);
  		  
  		  
  		}// if is doc
  		else{
        Map<String, Integer> docVector = new HashMap<String, Integer>();
        for(Token k : tokenList){   
          String key = k.getText();
          int value = k.getFrequency();
          length += value;
          docVector.put(key, value);
          
       
          
        }
        
        line.setDocLength(length);
        
        // new Term object
        
        line.setDocMap(docVector);
        line.setRel(doc.getRelevanceValue());
        line.setId(queryId);
        line.setSentence(doc.getText());
 
        // add to arraylist 
        eachD.add(line);
      }	
		}

	}

	/**
	 * TODO 1. Compute Cosine Similarity and rank the retrieved sentences 2.
	 * Compute the MRR metric
	 * 
	 * Compute Cosine Similarity of each doc and store their rank in Map rankId.
	 * 
	 */
	@Override
	public void collectionProcessComplete(ProcessTrace arg0)
			throws ResourceProcessException, IOException {

		super.collectionProcessComplete(arg0);

		// TODO :: compute the cosine similarity measure
		
		Line term = new Line();
		// put the info of last line to docData and tf
		docData.put(lastId, eachD);

		
    // output to file
    File outFile = null; 
    
    outFile = new File("task.txt");
    FileWriter fw = new FileWriter(outFile);
    BufferedWriter output = new BufferedWriter(fw);

    BM25 bm = new BM25();
    
    // first fulfill several variables
		for(int i = 1; i <= eachQ.size(); i++){
		  Map<String, Integer> queryVector = eachQ.get(i);
		  
		  // ArrayList answer of this query
		  ArrayList<Line> list = docData.get(i);
		  
		  // to store all answers of this query
		  Line [] array = new Line[list.size()];
	
		  int k = 0;
		  
		  // length of docs in one query
		  int docLength = 0;
		  
		  for(Line doc : list){
        docLength += doc.getDocLength(); 
      }
		  
		  
		  
		  for(Line docVector : list){
		    // compute itf of each word;
		    
		    
		    double itf = 0.0;
		    Iterator iter = queryVector.entrySet().iterator(); 
		    while (iter.hasNext()) { 
		      Map.Entry entry = (Map.Entry) iter.next(); 
		      
		      // q(i) = key
		      String key = (String)entry.getKey();
		      int val = (Integer)entry.getValue();
		      // value of itf
		      
		      
		      if(docVector.getDocMap().get(key) == null){
		        qf = 0.0;
		      }else{
		        qf = docVector.getDocMap().get(key);
		      }
		      
		      double wtf = 0.0;
		      

		      
		      
		      double nq = 0.0;
		      
		      double curDocLength = docVector.getDocLength();
		      
//		      wtf = 0;
		      
		      for(Line doc : list){
		        if(doc.getDocMap().get(key)!=null){
		          nq++; 
	            wtf += doc.getDocMap().get(key);
//	            System.out.println(key + wtf);
		        }
		      }
		      
		      itf += bm.score(qf, list.size(), curDocLength, docLength/list.size(), val, nq);
	//	        itf += bm.iscore(wtf, docLength, nq, qf, curDocLength/docLength, list.size(), qf, nq);
          
//		      System.out.print("wtf =" + qf);
//          System.out.print("  list.size() ="+ list.size());
//          System.out.print("  docLength =" + docLength);
//          System.out.print("  ave = " + curDocLength/docLength);
//          System.out.print("  qf =" + qf);
//          System.out.println("  nq =" + nq);
		      
          
          
		    }
		    docVector.setItf(itf);
		    
		    
//		    System.out.print("cosine="+ String.format("%.4f", docVector.getCos()));
//        System.out.println( "\tqid=" + 
//                docVector.getId() + "\trel=" + docVector.getRel() + "\t" + docVector.getSentence());
//		    // store doc into array
		    array[k] = docVector;
		    k++;
		  }
		  
		  int rank = 1;
		  
		  // sort array
		  term.insertionSort(array);
		  
		  // TODO :: compute the rank of retrieved sentences
		  

		  
		  for(int m = array.length - 1; m >= 0; m--){
		    if(array[m].getRel() == 0) continue;
		    else {
		      rank = array.length - m;
		      
		    
		      System.out.print("itf="+ String.format("%.4f", array[m].getItf()));
		      System.out.println("\trank=" + rank + "\tqid=" + 
		              array[m].getId() + "\t" + array[m].getSentence());


          
          
		      String result = "";
		      result += "itf=";
		      result += String.format("%.4f", array[m].getItf());
		      result += "\trank=";
		      result += rank;
		      result += "\tqid=";
		      result += array[m].getId();
		      result += "\trel=1\t";
		      result += array[m].getSentence();
		      
		      
		      
	        output.write(result);
	        output.newLine();
	        output.flush();
		      
		      break;
		    }
		  }
		  
		  
		  
		  idRank.put(i, rank);		  
		}
		
		
		// TODO :: compute the metric:: mean reciprocal rank
		double metric_mrr = compute_mrr();
		System.out.println(" (MRR) Mean Reciprocal Rank ::" + String.format("%.4f", metric_mrr));
		
		String metri = " (MRR) Mean Reciprocal Rank ::";
		
		metri += String.format("%.4f", metric_mrr);
		
		output.write(metri);
		
		output.close();
  }


	/**
	 * 
	 * @return mrr
	 */
	private double compute_mrr() {
		double metric_mrr=0.0;

		
		// TODO :: compute Mean Reciprocal Rank (MRR) of the text collection
		
		double sum = 0.000;
		
		for(int i = 1; i < idRank.size() + 1; i++){
		  sum += 1.0/idRank.get(i);
    }
		
		metric_mrr = sum/idRank.size();
		
		return metric_mrr;
	}

}
