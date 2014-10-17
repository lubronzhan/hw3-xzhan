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


public class RetrievalEvaluator extends CasConsumer_ImplBase {

	/** whole doc of different id **/
	public Map<Integer, ArrayList<Docu>> docData;
	
	/** doc of each id doc.
	 *  doc in arraylist 
	 *  list<token,frequency>
	 */
	public ArrayList<Docu> eachD;
	
	/** query of each id 
	 * 
	 * query in map  <queryId, <token, frequency>>
	 **/
	public Map<Integer, Map<String,Integer>> eachQ;
	
	/** store the last id **/
	public int lastId;

  /** rank of each id **/
	public Map<Integer, Integer> idRank;
	
	

	
		
	public void initialize() throws ResourceInitializationException {

		docData = new HashMap<Integer, ArrayList<Docu>>();
		
		eachD = new ArrayList<Docu>();
		
		eachQ = new HashMap<Integer, Map<String,Integer>>();
		
		idRank = new HashMap<Integer, Integer>();
		
		
		
		lastId = 1;

		
	}

	/**
	 * TODO :: 1. construct the global word dictionary 2. keep the word
	 * frequency for each sentence
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
			
	    // id of query
			Docu docu = new Docu();
			
			int queryId;
			queryId = doc.getQueryID();
			
			if(lastId != queryId){// another query id 
			  // add each in former trunk doc to data with last id
			  docData.put(lastId, eachD); 
			  
			  // new ArrayList to store doc with new id. 
			  eachD = new ArrayList<Docu>();
			  
//			  eachD.clear();
			  // update id.
			  lastId = queryId;
	  
			}
			
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
          docVector.put(k.getText(), k.getFrequency());
        }
        
        // new Docu object
        
        docu.setDocMap(docVector);
        docu.setRel(doc.getRelevanceValue());
        docu.setId(queryId);
        docu.setSentence(doc.getText());
        
      
        
        // add to arraylist 
        eachD.add(docu);
      }	
		}

	}

	/**
	 * TODO 1. Compute Cosine Similarity and rank the retrieved sentences 2.
	 * Compute the MRR metric
	 */
	@Override
	public void collectionProcessComplete(ProcessTrace arg0)
			throws ResourceProcessException, IOException {

		super.collectionProcessComplete(arg0);

		// TODO :: compute the cosine similarity measure
		
		Docu docu = new Docu();
		
		docData.put(lastId, eachD);
		
    // output to file
    File outFile = null; 
    
    outFile = new File("report.txt");
    FileWriter fw = new FileWriter(outFile);
    BufferedWriter output = new BufferedWriter(fw);

		for(int i = 1; i <= eachQ.size(); i++){
		  Map<String, Integer> queryVector = eachQ.get(i);
		  
		  
		  ArrayList<Docu> list = docData.get(i);

		  Docu [] array = new Docu[list.size()];
		  
		  int k = 0;
		  for(Docu docVector : list){
		    // compute the cosine of each doc
		    
		    docVector.setCos(computeCosineSimilarity(queryVector, docVector.getDocMap()));    
		    
		    // store doc into array
		    array[k] = docVector;
		    k++;
		  }
		  
		  int rank = 1;
		  
		  // sort array
		  docu.insertionSort(array);
		  
		  // TODO :: compute the rank of retrieved sentences
		  

		  
		  for(int m = array.length - 1; m >= 0; m--){
		    if(array[m].getRel() == 0) continue;
		    else {
		      rank = array.length - m;
		      
		      // system out line
		      System.out.print("cosine="+ String.format("%.4f", array[m].getCos()));
		      System.out.println("   rank= " + rank + "   qid= " + 
		              array[m].getId() + "   rel=1   " + array[m].getSentence());
		      
		      String result = "";
		      result += "cosine=";
		      result += String.format("%.4f", array[m].getCos());
		      result += "   rank= ";
		      result += rank;
		      result += "   qid= ";
		      result += array[m].getId();
		      result += "   rel=1   ";
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
		System.out.println(" (MRR) Mean Reciprocal Rank ::" + metric_mrr);
		
		String metri = " (MRR) Mean Reciprocal Rank ::";
		
		metri += metric_mrr;
		
		output.write(metri);
		
		output.close();
  }

	/**
	 * 
	 * @return cosine_similarity
	 */
	private double computeCosineSimilarity(Map<String, Integer> queryVector,
		Map<String, Integer> docVector) {
		double cosine_similarity=0.0, numerator = 0.0, denominator1 = 0.0, denominator2 = 0.0; 
		int temp1, temp2;
		// TODO :: compute cosine similarity between two sentences
		
		  
	  if ((queryVector.isEmpty()) || (docVector.isEmpty())) // if no word exist  
	  {  
	      return 0.0;  
	  }  
	    
	  Iterator queryIter = queryVector.entrySet().iterator();
	  while(queryIter.hasNext()){
	    Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>)queryIter.next();
	    String key = entry.getKey();
	    temp1 = entry.getValue();
	      
	    if(docVector.get(key) == null){
	      temp2 = 0;
	    }else{
	      temp2 = docVector.get(key);
	    }
	      
      docVector.remove(key);
      numerator += temp1 * temp2;  
	    denominator1 += temp1 * temp1;  
	    denominator2 += temp2 * temp2;
	      
	  }  
	    
	  Iterator docIter = docVector.entrySet().iterator();
	    
	  while(docIter.hasNext()){
	    Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>)docIter.next();
	
	    temp2 = entry.getValue();
	      
	    denominator2 += temp2 * temp2; 
	    
	  }
	  cosine_similarity = numerator / (Math.sqrt(denominator1 * denominator2));
	  return cosine_similarity;
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
