package com.dev.java6.immutable;

/**
 * The Class Main.
 */
public class Main {

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {

		final PersonManager.PersonBuilder personBuilder = new PersonManager.PersonBuilder();
		personBuilder.setPerson(new Person("Rupesh Raut", 31)).build();
		final Person person = personBuilder.getPerson();
		System.out.println(person);
	}
}
