package edu.cmu.lti.f14.hw3.hw3_xzhan.casconsumers;

import java.util.Comparator;
import java.util.Map;

public class Line implements Comparator<Line>{
  // store map
  private Map<String, Integer> docMap;
  
  //
  
  
  private int docLength;
  
  private double itf;
  
  private int rank;
  
  private int rel;
  
  private int id;
  
  
  
  private String sentence;
  
  public Line(){
    
  }
  

  public double getItf() {
    return itf;
  }

  public void setItf(double itf) {
    this.itf = itf;
  }

  public int getRank() {
    return rank;
  }

  public void setRank(int rank) {
    this.rank = rank;
  }

  public int getRel() {
    return rel;
  }

  public void setRel(int rel) {
    this.rel = rel;
  }


  public Map<String,Integer> getDocMap() {
    return docMap;
  }


  public void setDocMap(Map<String,Integer> docMap) {
    this.docMap = docMap;
  }


  public int getId() {
    return id;
  }


  public void setId(int id) {
    this.id = id;
  }


  @Override
  public int compare(Line arg0, Line arg1) {
    // TODO Auto-generated method stub
    if(arg0.getItf() < arg1.getItf())
    
    return 1;
    
    else return 0;
    
  }
  
  public void insertionSort(Line[] array){    
    for(int i = 0; i < array.length; i++){
      Line temp = array[i];
      int j;
      for(j = i - 1; j >= 0 && compare(temp, array[j]) == 1; j--){
        array[j + 1] = array[j];
      }
      array[j + 1] = temp;
    }
    
  }


  public String getSentence() {
    return sentence;
  }


  public void setSentence(String sentence) {
    this.sentence = sentence;
  }


  public int getDocLength() {
    return docLength;
  }


  public void setDocLength(int docLength) {
    this.docLength = docLength;
  }
  
}
