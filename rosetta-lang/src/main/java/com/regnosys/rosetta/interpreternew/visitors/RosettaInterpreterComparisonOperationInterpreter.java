package com.regnosys.rosetta.interpreternew.visitors;

import java.util.Arrays;
import java.util.List;

import com.regnosys.rosetta.interpreternew.values.RosettaInterpreterBaseValue;
import com.regnosys.rosetta.interpreternew.values.RosettaInterpreterBooleanValue;
import com.regnosys.rosetta.interpreternew.values.RosettaInterpreterError;
import com.regnosys.rosetta.interpreternew.values.RosettaInterpreterErrorValue;
import com.regnosys.rosetta.interpreternew.values.RosettaInterpreterIntegerValue;
import com.regnosys.rosetta.interpreternew.values.RosettaInterpreterListValue;
import com.regnosys.rosetta.interpreternew.values.RosettaInterpreterNumberValue;
import com.regnosys.rosetta.interpreternew.values.RosettaInterpreterStringValue;
import com.regnosys.rosetta.rosetta.expression.ComparisonOperation;
import com.regnosys.rosetta.rosetta.expression.RosettaExpression;
import com.regnosys.rosetta.rosetta.interpreter.RosettaInterpreterValue;

public class RosettaInterpreterComparisonOperationInterpreter extends 
	RosettaInterpreterConcreteInterpreter {
	
	private static List<String> comparisonOperators = 
			Arrays.asList("<", "<=", ">", ">=");
	
	/**
	 * Interprets a comparison operation, evaluating the comparison between two operands.
	 *
	 * @param expr The ComparisonOperation expression to interpret
	 * @return If no errors are encountered, a RosettaInterpreterBooleanValue representing
	 * 		   the result of the comparison operation.
	 * 		   If errors are encountered, a RosettaInterpreterErrorValue representing
     *         the error.
	 */
	public RosettaInterpreterBaseValue interp(ComparisonOperation expr) {
		if (!comparisonOperators.contains(expr.getOperator())) {
			return new RosettaInterpreterErrorValue(
					new RosettaInterpreterError(
							"operator not suppported")); 
		}
		RosettaExpression left = expr.getLeft();
		RosettaExpression right = expr.getRight();
		
		RosettaInterpreterValue leftValue = left.accept(visitor);
		RosettaInterpreterValue rightValue = right.accept(visitor);
		
		//check cardinality operation
		switch (expr.getCardMod()) {
		case NONE:
			//normally compare left and right side.
			boolean result = checkComparableTypes(leftValue, 
					rightValue, 
					expr.getOperator());
			return new RosettaInterpreterBooleanValue(result);
		
		case ANY:
			return compareAny(leftValue, rightValue, expr.getOperator());
			
		case ALL:
			return compareAll(leftValue, rightValue, expr.getOperator());

		default:
			return new RosettaInterpreterErrorValue(
					new RosettaInterpreterError(
							"cardinality modifier " + expr.getCardMod()
							+ " not supported"));
			
		}
		
		//check if these types are actually comparable 
//		boolean result = checkComparableTypes(leftValue, rightValue, expr.getOperator());
//		
//		return new RosettaInterpreterBooleanValue(result);
	}

	private RosettaInterpreterBaseValue compareAny(RosettaInterpreterValue leftValue, 
			RosettaInterpreterValue rightValue, 
			String operator) {
		//list vs list case:
		if (leftValue instanceof RosettaInterpreterListValue 
				&& rightValue instanceof RosettaInterpreterListValue) {
			
			//only way this is allowed is if rightValue has a length of 1
			// and left has length more than 1
			RosettaInterpreterListValue rgtList =
					(RosettaInterpreterListValue) rightValue;
			RosettaInterpreterListValue lfList = 
					(RosettaInterpreterListValue) leftValue;
			if (rgtList.getExpressions().size() == 1
					&& lfList.getExpressions().size() > 1) {
				
				
				//for all elements in left list, check if the comparison 
				// between them and right-hand side is true
				boolean anyTrue = true;
				for (RosettaInterpreterValue e : lfList.getExpressions()) {
					anyTrue |= checkComparableTypes(e, 
							rgtList.getExpressions().get(0), 
							operator);
				}
				return new RosettaInterpreterBooleanValue(anyTrue);
			}
			else {
				return new RosettaInterpreterErrorValue(
						new RosettaInterpreterError(
								"cannot compare two lists"));
			}
		}
		
		//list vs element case:
		else if (leftValue instanceof RosettaInterpreterListValue) {
			
			RosettaInterpreterListValue lfList = 
					(RosettaInterpreterListValue) leftValue;
			
			//only way this is allowed is if left side has a length of
			//  more than 1
			if (lfList.getExpressions().size() > 1) {
			
				//for all elements in left list, check if the comparison 
				// between them and right-hand side is true
				boolean anyTrue = true;
				for (RosettaInterpreterValue e : lfList.getExpressions()) {
					anyTrue |= checkComparableTypes(e, 
							rightValue, 
							operator);
				}
				return new RosettaInterpreterBooleanValue(anyTrue); 
			}
		}
		else {
			return new RosettaInterpreterErrorValue(
					new RosettaInterpreterError(
							"cannot use \"ANY\" keyword "
							+ "to compare two elements"));
		}
		return new RosettaInterpreterBooleanValue(false);
	}

	private RosettaInterpreterBaseValue compareAll(RosettaInterpreterValue leftValue, 
			RosettaInterpreterValue rightValue, 
			String operator) {
		//list vs list case:
		if (leftValue instanceof RosettaInterpreterListValue 
				&& rightValue instanceof RosettaInterpreterListValue) {
			
			//only way this is allowed is if rightValue has a length of 1
			// and left has length more than 1
			RosettaInterpreterListValue rgtList =
					(RosettaInterpreterListValue) rightValue;
			RosettaInterpreterListValue lfList = 
					(RosettaInterpreterListValue) leftValue;
			if (rgtList.getExpressions().size() == 1
					&& lfList.getExpressions().size() > 1) {
				
				
				//for all elements in left list, check if the comparison 
				// between them and right-hand side is true
				boolean allTrue = true;
				for (RosettaInterpreterValue e : lfList.getExpressions()) {
					allTrue &= checkComparableTypes(e, 
							rgtList.getExpressions().get(0), 
							operator);
				}
				return new RosettaInterpreterBooleanValue(allTrue);
			}
			else {
				return new RosettaInterpreterErrorValue(
						new RosettaInterpreterError(
								"cannot compare two lists"));
			}
		}
		
		//list vs element case:
		else if (leftValue instanceof RosettaInterpreterListValue) {
			
			RosettaInterpreterListValue lfList = 
					(RosettaInterpreterListValue) leftValue;
			
			//only way this is allowed is if left side has a length of
			//  more than 1
			if (lfList.getExpressions().size() > 1) {
			
				//for all elements in left list, check if the comparison 
				// between them and right-hand side is true
				boolean allTrue = true;
				for (RosettaInterpreterValue e : lfList.getExpressions()) {
					allTrue &= checkComparableTypes(e, 
							rightValue, 
							operator);
				}
				return new RosettaInterpreterBooleanValue(allTrue); 
			}
		}
		else {
			return new RosettaInterpreterErrorValue(
					new RosettaInterpreterError(
							"cannot use \"ALL\" keyword "
							+ "to compare two elements"));
		}
		return new RosettaInterpreterBooleanValue(false);
	}

	private boolean checkComparableTypes(RosettaInterpreterValue leftValue, 
			RosettaInterpreterValue rightValue,
			String operator) {
		int comparisonResult = 2;
		
		//compare integers
		if (leftValue instanceof RosettaInterpreterIntegerValue 
				&&  rightValue instanceof RosettaInterpreterIntegerValue) {
			RosettaInterpreterIntegerValue leftInt = 
					(RosettaInterpreterIntegerValue) leftValue;
			RosettaInterpreterIntegerValue rightInt = 
					(RosettaInterpreterIntegerValue) rightValue;
			
			comparisonResult = leftInt.compareTo(rightInt);
		}
		
		//compare booleans
		else if (leftValue instanceof RosettaInterpreterBooleanValue 
				&&  rightValue instanceof RosettaInterpreterBooleanValue) {
			RosettaInterpreterBooleanValue leftBool = 
					(RosettaInterpreterBooleanValue) leftValue;
			RosettaInterpreterBooleanValue rightBool = 
					(RosettaInterpreterBooleanValue) rightValue;
			
			comparisonResult = leftBool.compareTo(rightBool);
		}
		
		//compare strings
		else if (leftValue instanceof RosettaInterpreterStringValue 
				&&  rightValue instanceof RosettaInterpreterStringValue) {
			RosettaInterpreterStringValue leftString = 
					(RosettaInterpreterStringValue) leftValue;
			RosettaInterpreterStringValue rightString = 
					(RosettaInterpreterStringValue) rightValue;
			
			comparisonResult = leftString.compareTo(rightString);
		}
		
		//compare numbers
		else if (leftValue instanceof RosettaInterpreterNumberValue 
				&&  rightValue instanceof RosettaInterpreterNumberValue) {
			RosettaInterpreterNumberValue leftNumber = 
					(RosettaInterpreterNumberValue) leftValue;
			RosettaInterpreterNumberValue rightNumber = 
					(RosettaInterpreterNumberValue) rightValue;
			
			comparisonResult = leftNumber.compareTo(rightNumber);
		}
		
		
		return compareComparableValues(comparisonResult, 
				operator);
	}

	private boolean compareComparableValues(int comparisonResult, String operator) {
//		if (comparisonResult == 2) { 
//			//should not happen, means classes are not comparable
//			return false; //TODO: should throw exception
//		}
		switch (operator) {
		case "<":
			return comparisonResult == -1;
		case "<=":
			return comparisonResult == -1 || comparisonResult == 0;
		case ">":
			return comparisonResult == 1;
		case ">=":
			return comparisonResult == 1 || comparisonResult == 0;
		default:
			return false; //should never happen
		}
	}

}