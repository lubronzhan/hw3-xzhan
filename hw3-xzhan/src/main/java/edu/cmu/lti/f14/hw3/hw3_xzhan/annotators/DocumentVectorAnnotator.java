package edu.cmu.lti.f14.hw3.hw3_xzhan.annotators;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.IntegerArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.lti.f14.hw3.hw3_xzhan.typesystems.Document;
import edu.cmu.lti.f14.hw3.hw3_xzhan.typesystems.Token;
import edu.cmu.lti.f14.hw3.hw3_xzhan.utils.StanfordLemmatizer;
import edu.cmu.lti.f14.hw3.hw3_xzhan.utils.Utils;

public class DocumentVectorAnnotator extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		FSIterator<Annotation> iter = jcas.getAnnotationIndex().iterator();
		if (iter.isValid()) {
			iter.moveToNext();
			Document doc = (Document) iter.get();
			try {
        createTermFreqVector(jcas, doc);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
		}

	}
	
	
	/**
   * A basic white-space tokenizer, it deliberately does not split on punctuation!
   *
   * @param doc input text
   * @return    a list of tokens.
   */

  List<String> tokenize0(String doc) {
    List<String> res = new ArrayList<String>();
    
    for (String s: doc.split("\\s+"))
      res.add(s);
    return res;
  }
  
  
	/**
	 * 
	 * @param jcas
	 * @param doc
	 * @throws IOException 
	 */

	private void createTermFreqVector(JCas jcas, Document doc) throws IOException {

	  String docText = doc.getText();
//		String docText = doc.getText().replace(".", "").replace("!", "").replace("?", "")
//		        .replace(",", "").replace(":", "").replace("'s", "").replace("\"", "")
//		        .replace("--"," ").replace("-", " ").replace(";", "")
//            ;
		
		//TO DO: construct a vector of tokens and update the tokenList in CAS
		
		// split each word
	  List<String> list = tokenize0(docText);
//		List<String> list = tokenize0(StanfordLemmatizer.stemText(docText));
		
		
		// store each word's frequency
		Map<String, Integer> map = new HashMap<String, Integer>();
		
//    Map<String, Integer> stopWord = new HashMap<String, Integer>();
//    
//    FileReader fr=new FileReader("src/main/resources/stopwords.txt");
//    BufferedReader br=new BufferedReader(fr);
//    String line="";
  
//    while ((line=br.readLine())!=null) {
//        stopWord.put(line, 1);
//    }
//    br.close();
//    fr.close();
		
		for(String w : list){
		  // if(doc.getRelevanceValue() == 99);
		  
//		  if(stopWord.containsKey(w))
//		    continue;
		  
		  
		  if(map.containsKey(w)){
		    map.put(w, map.get(w) + 1);
		  }
		  else{
		    map.put(w, 1);
		  }
		}

		// each fslist
		FSList fs = new FSList(jcas);
		
		ArrayList<Token> tokenList = new ArrayList<Token>();
		
		
		
		// set each token
		for(String key : list){
	    // each token
		  
//		  if(stopWord.containsKey(key))
//        continue;
	    Token t = new Token(jcas);
		  t.setText(key);
		  t.setFrequency(map.get(key));
		  t.addToIndexes();
		  tokenList.add(t);
		  
		}
		
		fs = Utils.fromCollectionToFSList(jcas,tokenList);
		// add fs to doc
		doc.setTokenList(fs);
		
		// add fs to index
		fs.addToIndexes();
		
		// update the doc fs feature, first remove then add
//		jcas.removeFsFromIndexes(doc);
		jcas.addFsToIndexes(doc);
		
		

	}

}
