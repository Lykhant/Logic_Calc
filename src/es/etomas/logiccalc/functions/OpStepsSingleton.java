package es.etomas.logiccalc.functions;

import java.util.ArrayList;
import java.util.List;

public class OpStepsSingleton {
	
	private static OpStepsSingleton instance = null;
	private List<String> opSteps;
	private List<String> explainedSteps;
	
	private OpStepsSingleton() {
		this.opSteps = new ArrayList<>();
		this.explainedSteps = new ArrayList<>();
	}

	public static OpStepsSingleton getInstance() {
		if(instance == null) {
	         instance = new OpStepsSingleton();
		}
		return instance;
	}
	
	public void addStep(String step) {
		this.opSteps.add(step);
	}
	
	public List<String> getExplainedSteps() {
		return this.explainedSteps;
	}
	
	public List<String> getSteps() {
		return opSteps;
	}

	public void addExplanation(String explanation) {
		this.explainedSteps.add(explanation);
		
	}

	public void reset() {
		this.opSteps = new ArrayList<>();
		this.explainedSteps = new ArrayList<>();
	}
	
}
