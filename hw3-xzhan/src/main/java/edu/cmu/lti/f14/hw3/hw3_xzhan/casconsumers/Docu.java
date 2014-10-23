package edu.cmu.lti.f14.hw3.hw3_xzhan.casconsumers;

/**
 * This class is used to store each line of document.
 *
 * Implements Comparator so that instance of Docu could be compared
 *
 *  Name : Xiaoran Zhan (xzhan)
 *
 **/

import java.util.Comparator;
import java.util.Map;

public class Docu implements Comparator<Docu>{
  // store map
  private Map<String,Integer> docMap;
  
  //

  /** cosine similarity of this document **/
  private double cos;
  /** rank of this document in whole document **/
  private int rank;
  /** relevance of this document **/
  private int rel;
  /** query id of this document **/
  private int id;
  /** text content of this document **/
  private String sentence;
  
  public Docu(){
    
  }
  

  public double getCos() {
    return cos;
  }

  public void setCos(double cos) {
    this.cos = cos;
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

  /**
   * 
   * Compare two Docu instance
   * 
   * If Docu1.cos < Docu2.cos
   * 
   * @return 1
   * 
   **/
  @Override
  public int compare(Docu arg0, Docu arg1) {
    // TODO Auto-generated method stub
    if(arg0.getCos() < arg1.getCos())
    
    return 1;
    
    else return 0;
    
  }
  
  /**
   * 
   * Sort an array of Docu
   * 
   * Use insertion sort.
   * 
   **/
  public void insertionSort(Docu[] array){    
    for(int i = 0; i < array.length; i++){
      Docu temp = array[i];
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
  
}
