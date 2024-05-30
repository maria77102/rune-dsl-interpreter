package com.regnosys.rosetta.interpreternew;

import javax.inject.Inject;

import com.regnosys.rosetta.interpreternew.values.RosettaInterpreterEnvironment;
import com.regnosys.rosetta.interpreternew.values.RosettaInterpreterValueEnvironmentTuple;
import com.regnosys.rosetta.rosetta.expression.RosettaExpression;

public class RosettaInterpreterNew {
	
	@Inject
	private RosettaInterpreterVisitor visitor;
	
	/**
	 * Simple example interpret function to allow for better understanding 
	 * of the development workflow.
	 *
	 * @param expression the expression to be interpreted
	 * @return value of RosettaIntLiteral otherwise exception
	 */
	
	public RosettaInterpreterValueEnvironmentTuple interp(RosettaExpression expression, 
			RosettaInterpreterEnvironment env) {
		return (RosettaInterpreterValueEnvironmentTuple)
				expression.accept(visitor, env);	
	}
	
	public RosettaInterpreterValueEnvironmentTuple interp(RosettaExpression expression) {
		return (RosettaInterpreterValueEnvironmentTuple)
				expression.accept(visitor, new RosettaInterpreterEnvironment());	
	}
}
