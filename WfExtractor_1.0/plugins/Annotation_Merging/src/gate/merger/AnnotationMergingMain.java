package gate.merger;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;
import gate.ProcessingResource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.util.AnnotationMerging;
import gate.util.InvalidOffsetException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;
//import gate.util.IaaCalculation;

public class AnnotationMergingMain extends AbstractLanguageAnalyser implements
                                                                   ProcessingResource {

  private static final long serialVersionUID = -5149313306167125053L;

  /** Annotation sets for merging in one document. */
  private String annSetsForMerging;

  /** Annotation set for merged annotations. */
  private String annSetOutput;

  /** Merging method. */
  private MergingMethodsEnum mergingMethod;

  /**
   * Minimal number of annotators to agree for the MergingByAnnotatorNum method
   */
  private String minimalAnnNum;

  /** Specifying the annotation types and features for merging. */
  private String annTypesAndFeats;

  /** Should source annotations be kept when merged? */
  private Boolean keepSourceForMergedAnnotations;

  /** Initialise this resource, and return it. */
  public gate.Resource init() throws ResourceInstantiationException {
    return this;
  } // init()

  /**
   * Run the resource.
   * 
   * @throws ExecutionException
   */
  public void execute() throws ExecutionException {
    // get the annotation sets for merging
    String termSeparator = ";";
    // Get all the existing annotation sets from the current document
    Set<String> annsExisting = document.getAnnotationSetNames();
    String[] annsArray;
    if(annSetsForMerging == null || annSetsForMerging.trim().length() == 0) {
      // throw new ExecutionException("No annotation set was specified for
      // merging!");
      int num = 0;
      for(Object obj : annsExisting) {
        if(obj!= null && obj.toString().trim().length()>0)
          ++num;
      }
      annsArray = new String[num];
      num=0;
      List<String>annsE = new Vector<String>(annsExisting);
      Collections.sort(annsE);
      for(Object obj : annsE) {
        if(obj!= null && obj.toString().trim().length()>0)
          annsArray[num++] = obj.toString();
      }
    }
    else {
      annSetsForMerging = annSetsForMerging.trim();
      annsArray = annSetsForMerging.split(termSeparator);
    }
    int numAnns = annsArray.length;
    for(int i = 0; i < numAnns; ++i)
      annsArray[i] = annsArray[i].trim();

    // Check if each annotation set for merging exists in the current
    // document
    for(int i = 0; i < numAnns; ++i)
      if(!annsExisting.contains(annsArray[i]))
        throw new ExecutionException("The annotation set" + annsArray[i]
          + "for merging doesn't exist in current document "
          + document.getName());
    // Collect the annotation types from annotation sets for merging
    HashMap<String, String> annsTypes = new HashMap<String, String>();
    if(this.annTypesAndFeats == null
      || this.annTypesAndFeats.trim().length() == 0)
      for(int i = 0; i < numAnns; ++i) {
        Set<String> types = document.getAnnotations(annsArray[i]).getAllTypes();
        for(String obj : types)
          if(!annsTypes.containsKey(obj)) annsTypes.put(obj, null);
      }
    else {
      String[] annTs = this.annTypesAndFeats.split(termSeparator);
      for(int i = 0; i < annTs.length; ++i) {
        annTs[i] = annTs[i].trim();
        if(annTs[i].contains("->")) {
          String ty = annTs[i].substring(0, annTs[i].indexOf("->"));
          String tf = annTs[i].substring(annTs[i].indexOf("->") + 2);
          annsTypes.put(ty.trim(), tf.trim());
        }
        else annsTypes.put(annTs[i], null);
      }
    }
    // merging annotation for each annotation type and put it into the
    // merged annotation set.
    int minimalAnnNumInt = 1;
    if(minimalAnnNum != null && minimalAnnNum.trim().length() > 0) {
      if(Integer.parseInt(minimalAnnNum) < 1) 
        minimalAnnNumInt = 1;
      else if (Integer.parseInt(minimalAnnNum) > numAnns)
        minimalAnnNumInt = numAnns;
      else minimalAnnNumInt = Integer.parseInt(minimalAnnNum);
    }
    else minimalAnnNumInt = 1;
    AnnotationSet annsDoc = document.getAnnotations(this.annSetOutput);
    for(String annT : annsTypes.keySet()) {
      // collect the annotation set for the current type for merging
      AnnotationSet[] annsA = new AnnotationSet[numAnns];
      for(int i = 0; i < numAnns; ++i) {
        AnnotationSet anns = document.getAnnotations(annsArray[i]);
        if(anns.get(annT) != null) annsA[i] = anns.get(annT);
      }
      boolean isTheSameInstances = AnnotationMerging.isSameInstancesForAnnotators(
        annsA, 0);
      HashMap<Annotation, String> mergeInfor = new HashMap<Annotation, String>();
      //Call different merging methods
      switch(mergingMethod){
        case MajorityVoting:
          AnnotationMerging.mergeAnnotationMajority(annsA, annsTypes.get(annT),
            mergeInfor, isTheSameInstances);
          break;
        case MergingByAnnotatorNum:
          AnnotationMerging.mergeAnnotation(annsA, annsTypes.get(annT),
            mergeInfor, minimalAnnNumInt, isTheSameInstances);
          break;
        default:
          throw new ExecutionException("The merging method is not defined!");
      }
      if(annSetOutput != null && annSetOutput.trim().length() != 0)
        document.getAnnotations(annSetOutput);
      else document.getAnnotations("mergedAnns");
      //Add the merged annotations
      for(Annotation ann : mergeInfor.keySet()) {
        if (!keepSourceForMergedAnnotations) {
          // for each source annotation set
          for(String ASName : annsArray) {
            AnnotationSet sourceAS = document.getAnnotations(ASName);
            // find source annotations for the annotation merged
            // based only on their offsets
            AnnotationSet containedAS = sourceAS.getContained(
              ann.getStartNode().getOffset(), ann.getEndNode().getOffset());
            for (Annotation annotation : containedAS) {
              if (annotation.coextensive(ann)) {
                // delete source annotations
                sourceAS.remove(annotation);
              }
            }
          }
        }
        FeatureMap featM = Factory.newFeatureMap();
        FeatureMap feat0 = ann.getFeatures();
        for(Object obj : feat0.keySet()) {
          featM.put(obj, feat0.get(obj));
        }
        // Get the annotators (annotation set name for each merged annotation)
        String[] annIndex = mergeInfor.get(ann).split("-");
        StringBuffer annNames = new StringBuffer();
        for(int i = 0; i < annIndex.length; ++i) {
          if(i > 0) annNames.append("/");
          annNames.append(annsArray[Integer.valueOf(annIndex[i])]);
        }
        featM.put("annotators", annNames.toString());
        try {
          annsDoc.add(ann.getStartNode().getOffset(), ann.getEndNode()
            .getOffset(), annT, featM);
        }
        catch(InvalidOffsetException e) {
          e.printStackTrace();
        }
      }
      // remove the annotator features from the first annotation set
      AnnotationSet annsRe = document.getAnnotations(annsArray[0]).get(annT);
      for(Annotation ann : annsRe) {
        ann.getFeatures().remove("annotators");
      }

    }// End the loop for annotation type

  }

  


  public void setAnnSetsForMerging(String annSetSeq) {
    this.annSetsForMerging = annSetSeq;
  }

  public String getAnnSetsForMerging() {
    return this.annSetsForMerging;
  }

  public void setAnnSetOutput(String annSet) {
    this.annSetOutput = annSet;
  }

  public String getAnnSetOutput() {
    return this.annSetOutput;
  }

  public void setAnnTypesAndFeats(String annTypeSeq) {
    this.annTypesAndFeats = annTypeSeq;
  }

  public String getAnnTypesAndFeats() {
    return this.annTypesAndFeats;
  }

  public MergingMethodsEnum getMergingMethod() {
    return this.mergingMethod;
  }

  public void setMergingMethod(MergingMethodsEnum m) {
    this.mergingMethod = m;
  }

  public String getMinimalAnnNum() {
    return this.minimalAnnNum;
  }

  public void setMinimalAnnNum(String n) {
    this.minimalAnnNum = n;
  }

  public Boolean getkeepSourceForMergedAnnotations() {
    return this.keepSourceForMergedAnnotations;
  }

  public void setkeepSourceForMergedAnnotations(Boolean b) {
    this.keepSourceForMergedAnnotations = b;
  }

}
