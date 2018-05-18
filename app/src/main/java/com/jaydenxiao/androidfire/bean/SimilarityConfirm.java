package com.jaydenxiao.androidfire.bean;

import com.minivision.facerecognitionlib.entity.RecognizedFace;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by xtt on 2017/9/25.
 */

public class SimilarityConfirm implements Serializable{

    private String Name;
    ArrayList<RecognizedFace> recognitionFaces;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public ArrayList<RecognizedFace> getRecognitionFaces() {
        return recognitionFaces;
    }

    public void setRecognitionFaces(ArrayList<RecognizedFace> recognitionFaces) {
        this.recognitionFaces = recognitionFaces;
    }
}
