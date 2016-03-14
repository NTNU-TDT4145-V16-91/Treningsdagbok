package no.ntnu.stud.tdt4145.gruppe91;

import java.util.function.Consumer;
import java.util.function.Function;

public interface UiUtility {

	public <E> E getUserInput(Consumer<E> testInput, Function<String, E> converter) throws UserCancelException;
	public <E> E getUserInput(Function<String, E> converter) throws UserCancelException;
	public String getUserString() throws UserCancelException;
	public String getUserString(Consumer<Integer> testInput) throws UserCancelException;
	public int getUserInt() throws UserCancelException;
	public int getUserInt(Consumer<Integer> testInput) throws UserCancelException;
	public boolean getUserBoolean(String trueText, String falseText) throws UserCancelException;
}
