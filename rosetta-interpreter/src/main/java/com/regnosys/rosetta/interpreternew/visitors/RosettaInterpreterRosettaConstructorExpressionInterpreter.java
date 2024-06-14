package com.regnosys.rosetta.interpreternew.visitors;

import org.eclipse.emf.common.util.EList;

import com.regnosys.rosetta.interpreternew.values.RosettaInterpreterBaseValue;
import com.regnosys.rosetta.interpreternew.values.RosettaInterpreterDateTimeValue;
import com.regnosys.rosetta.interpreternew.values.RosettaInterpreterDateValue;
import com.regnosys.rosetta.interpreternew.values.RosettaInterpreterError;
import com.regnosys.rosetta.interpreternew.values.RosettaInterpreterErrorValue;
import com.regnosys.rosetta.interpreternew.values.RosettaInterpreterNumberValue;
import com.regnosys.rosetta.interpreternew.values.RosettaInterpreterStringValue;
import com.regnosys.rosetta.interpreternew.values.RosettaInterpreterTimeValue;
import com.regnosys.rosetta.interpreternew.values.RosettaInterpreterZonedDateTimeValue;
import com.regnosys.rosetta.rosetta.interpreter.RosettaInterpreterBaseEnvironment;
import com.regnosys.rosetta.rosetta.expression.ConstructorKeyValuePair;
import com.regnosys.rosetta.rosetta.expression.RosettaConstructorExpression;
import com.regnosys.rosetta.rosetta.interpreter.RosettaInterpreterValue;

public class RosettaInterpreterRosettaConstructorExpressionInterpreter extends RosettaInterpreterConcreteInterpreter {

	/**
	 * Interpreter method for Constructor Expressions.
	 *
	 * @param expr 		RosettaConstructorExpression to be interpreted
	 * @param env		the environment used
	 * @return 			the interpreted value
	 */
	public RosettaInterpreterBaseValue interp(
			RosettaConstructorExpression expr, RosettaInterpreterBaseEnvironment env) {
		String typeCall = expr.getTypeCall().getType().getName();
		EList<ConstructorKeyValuePair> values = expr.getValues();
		
		switch (typeCall) {
			case "time": {
				RosettaInterpreterValue day = values.get(0).getValue().accept(visitor, env);
				RosettaInterpreterValue month = values.get(1).getValue().accept(visitor, env);
				RosettaInterpreterValue year = values.get(2).getValue().accept(visitor, env);
				
				if (day instanceof RosettaInterpreterNumberValue 
						&& month instanceof RosettaInterpreterNumberValue 
						&& year instanceof RosettaInterpreterNumberValue) {
					return new RosettaInterpreterDateValue(
							((RosettaInterpreterNumberValue) day), 
							((RosettaInterpreterNumberValue) month),
							((RosettaInterpreterNumberValue) year));
				}
				break;
			}
			case "date": {
				RosettaInterpreterValue day = values.get(0).getValue().accept(visitor, env);
				RosettaInterpreterValue month = values.get(1).getValue().accept(visitor, env);
				RosettaInterpreterValue year = values.get(2).getValue().accept(visitor, env);
				
				if (day instanceof RosettaInterpreterNumberValue 
						&& month instanceof RosettaInterpreterNumberValue 
						&& year instanceof RosettaInterpreterNumberValue) {
					return new RosettaInterpreterDateValue(
							((RosettaInterpreterNumberValue) day), 
							((RosettaInterpreterNumberValue) month),
							((RosettaInterpreterNumberValue) year));
				}
				break;
			}
			case "dateTime": {
				RosettaInterpreterValue date = values.get(0).getValue().accept(visitor, env);
				RosettaInterpreterValue time = values.get(1).getValue().accept(visitor, env);
				
				if (date instanceof RosettaInterpreterDateValue 
						&& time instanceof RosettaInterpreterTimeValue) {
					boolean check = ((RosettaInterpreterTimeValue) time).valid();
					
					if (!check) {
						return new RosettaInterpreterErrorValue(new RosettaInterpreterError(
								"Constructor Expressions: time isn't valid."));
					} else {
						return new RosettaInterpreterDateTimeValue(
							((RosettaInterpreterDateValue) date), 
							((RosettaInterpreterTimeValue) time));
					}
				}
				break;
			}
			case "zonedDateTime": {
				RosettaInterpreterValue date = values.get(0).getValue().accept(visitor, env);
				RosettaInterpreterValue time = values.get(1).getValue().accept(visitor, env);
				RosettaInterpreterValue timeZone = values.get(2).getValue().accept(visitor, env);
				
				if (date instanceof RosettaInterpreterDateValue 
						&& time instanceof RosettaInterpreterTimeValue 
						&& timeZone instanceof RosettaInterpreterStringValue) {
					boolean check = ((RosettaInterpreterTimeValue) time).valid();
					
					if (!check) {
						return new RosettaInterpreterErrorValue(new RosettaInterpreterError(
								"Constructor Expressions: time isn't valid."));
					} else {
						return new RosettaInterpreterZonedDateTimeValue(
							((RosettaInterpreterDateValue) date), 
							((RosettaInterpreterTimeValue) time),
							((RosettaInterpreterStringValue) timeZone));
					}
				}
				break;
			}
			default: {
				// needed for data types
				break;
			}
		}
		
		return new RosettaInterpreterErrorValue(new RosettaInterpreterError(
				"Constructor Expressions: constructor doesn't exist."));
	}
}
