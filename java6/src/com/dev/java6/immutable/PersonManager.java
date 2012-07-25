package com.dev.java6.immutable;

/**
 * The Class PersonManager.
 */
public class PersonManager {

	/** The person. */
	private final Person person;

	/**
	 * Instantiates a new person manager.
	 * 
	 * @param personBuilder
	 *            the person builder
	 */
	public PersonManager(PersonBuilder personBuilder) {
		super();
		this.person = personBuilder.person;
	}

	/**
	 * Gets the person.
	 * 
	 * @return the person
	 */
	public Person getPerson() {
		return person;
	}

	/**
	 * The Class PersonBuilder.
	 */
	public static class PersonBuilder implements Builder<PersonManager> {

		/** The person. */
		private Person person;

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.dev.java6.immutable.Builder#build()
		 */
		@Override
		public PersonManager build() {
			return new PersonManager(this);
		}

		/**
		 * Gets the person.
		 * 
		 * @return the person
		 */
		public Person getPerson() {
			return person;
		}

		/**
		 * Sets the person.
		 * 
		 * @param person
		 *            the person
		 * @return the person builder
		 */
		public PersonBuilder setPerson(Person person) {
			this.person = person;
			return this;
		}

	}

}
