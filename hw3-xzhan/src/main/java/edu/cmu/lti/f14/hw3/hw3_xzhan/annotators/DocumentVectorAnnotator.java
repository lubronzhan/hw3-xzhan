package edu.cmu.lti.f14.hw3.hw3_xzhan.annotators;

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
import edu.cmu.lti.f14.hw3.hw3_xzhan.utils.Utils;

public class DocumentVectorAnnotator extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		FSIterator<Annotation> iter = jcas.getAnnotationIndex().iterator();
		if (iter.isValid()) {
			iter.moveToNext();
			Document doc = (Document) iter.get();
			createTermFreqVector(jcas, doc);
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
	 */

	private void createTermFreqVector(JCas jcas, Document doc) {

		String docText = doc.getText();
		
		//TO DO: construct a vector of tokens and update the tokenList in CAS
		
		// split each word
		List<String> list = tokenize0(docText);
		
		
		// store each word's frequency
		Map<String, Integer> map = new HashMap<String, Integer>();
		
		for(String w : list){
		  // if(doc.getRelevanceValue() == 99);
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
