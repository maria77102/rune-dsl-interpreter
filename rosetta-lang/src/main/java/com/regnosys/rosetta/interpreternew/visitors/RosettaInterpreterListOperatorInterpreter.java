package com.regnosys.rosetta.interpreternew.visitors;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.regnosys.rosetta.interpreternew.values.RosettaInterpreterBaseValue;
import com.regnosys.rosetta.interpreternew.values.RosettaInterpreterError;
import com.regnosys.rosetta.interpreternew.values.RosettaInterpreterErrorValue;
import com.regnosys.rosetta.interpreternew.values.RosettaInterpreterIntegerValue;
import com.regnosys.rosetta.interpreternew.values.RosettaInterpreterListValue;
import com.regnosys.rosetta.interpreternew.values.RosettaInterpreterNumberValue;
import com.regnosys.rosetta.rosetta.expression.DistinctOperation;
import com.regnosys.rosetta.rosetta.expression.ReverseOperation;
import com.regnosys.rosetta.rosetta.expression.RosettaExpression;
import com.regnosys.rosetta.rosetta.expression.SumOperation;
import com.regnosys.rosetta.rosetta.interpreter.RosettaInterpreterValue;

public class RosettaInterpreterListOperatorInterpreter 
	extends RosettaInterpreterConcreteInterpreter {

	/**
	 * Interprets a Distinct Operation.
	 * Returns a list where duplicate elements are removed
	 * such that only one copy of them exists in the resulting
	 * list
	 *
	 * @param exp - distinct operation to interpret
	 * @return - list Value of distinct values
	 */
	public RosettaInterpreterValue interp(DistinctOperation exp) {
		RosettaExpression expression = exp.getArgument();
		RosettaInterpreterValue val = expression.accept(visitor);
		
		if (RosettaInterpreterErrorValue.errorsExist(val)) {
			return RosettaInterpreterErrorValue.merge(val);
		}
		
		List<RosettaInterpreterValue> distinct = 
				RosettaInterpreterBaseValue.valueStream(val)
				.distinct()
				.collect(Collectors.toList());
		
		return new RosettaInterpreterListValue(distinct);
	}

	/**
	 * Interprets a Reverse operation.
	 * Reverses the order of the elements in the list and returns it
	 *
	 * @param exp - Reverse operation to interpret
	 * @return - Reversed list
	 */
	public RosettaInterpreterValue interp(ReverseOperation exp) {
		RosettaExpression expression = exp.getArgument();
		RosettaInterpreterValue val = expression.accept(visitor);
		
		if (RosettaInterpreterErrorValue.errorsExist(val)) {
			return RosettaInterpreterErrorValue.merge(val);
		}
		
		List<RosettaInterpreterValue> values =
				RosettaInterpreterBaseValue.toValueList(val);
		Collections.reverse(values);
		
		return new RosettaInterpreterListValue(values);
	}

	/**
	 * Interprets the sum operation.
	 * Returns a sum of all the summable elements of the list
	 * If the elements are not summable returns an error
	 *
	 * @param exp - Sum operation to interpret
	 * @return sum of elements or error if elements are not summable
	 */
	public RosettaInterpreterValue interp(SumOperation exp) {
		RosettaExpression expression = exp.getArgument();
		RosettaInterpreterValue val = expression.accept(visitor);
		
		if (RosettaInterpreterErrorValue.errorsExist(val)) {
			return RosettaInterpreterErrorValue.merge(val);
		}
		
		// In the compiler, this returns a null rather than an error
		// So I'm not exactly sure how to handle it
		if (RosettaInterpreterBaseValue.toValueList(val).size() < 1) {
			return new RosettaInterpreterErrorValue(
					new RosettaInterpreterError("Cannot take sum"
							+ " of empty list"));
		}
		
		List<RosettaInterpreterValue> values =
				RosettaInterpreterBaseValue.toValueList(val);
		
		// Check that all values are numbers, and convert ints
		// to numbers for further simplicity
		for (int i = 0; i < values.size(); i++) {
			RosettaInterpreterValue v = values.get(i);
			if (v instanceof RosettaInterpreterIntegerValue) {
				RosettaInterpreterIntegerValue valInt =
						(RosettaInterpreterIntegerValue)v;
				values.set(i, new RosettaInterpreterNumberValue(
						BigDecimal.valueOf(valInt.getValue().longValue())));
			}
			else if (!(v instanceof RosettaInterpreterNumberValue)) {
				return new RosettaInterpreterErrorValue(
						new RosettaInterpreterError("Cannot take sum"
								+ "of non-number value"));
			}
		}
		
		BigDecimal result = values.stream()
				.map(x -> ((RosettaInterpreterNumberValue)x).getValue())
				.reduce(BigDecimal.valueOf(0), (x, y) -> x.add(y));
		
		return new RosettaInterpreterNumberValue(result);
	}
	

}
