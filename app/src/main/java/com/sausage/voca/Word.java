package com.sausage.voca;

public class Word{
    private String englishWord;
    private String wordMean1;
    private String wordMean2;
    private String wordMean3;
    private int memorization;

    public Word(String wordE,String wordM1, String wordM2,String wordM3,int memorize){
        this.englishWord=wordE;
        this.wordMean1=wordM1;
        this.wordMean2=wordM2;
        this.wordMean3=wordM3;
        this.memorization=memorize;
    }
    public String getEnglishWord(){
        return englishWord;
    }
    public String getWordMean1(){
        return wordMean1;
    }
    public String getWordMean2(){
        return wordMean2;
    }
    public String getWordMean3(){
        return wordMean3;
    }
    public int getMemorization(){
        return memorization;
    }
    public void setEnglishWord(String englishWord){
        this.englishWord = englishWord;
    }
    public void setWordMean1(String wordMean1){
        this.wordMean1 = wordMean1;
    }
    public void setWordMean2(String wordMean2){
        this.wordMean2 = wordMean2;
    }
    public void setWordMean3(String wordMean3){
        this.wordMean3 = wordMean3;
    }
    public void setMemorization(int memorization){
        this.memorization = memorization;
    }
}
