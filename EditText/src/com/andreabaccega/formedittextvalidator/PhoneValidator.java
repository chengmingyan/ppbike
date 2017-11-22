package com.andreabaccega.formedittextvalidator;

import java.util.regex.Pattern;
/**
 * It validates phone numbers.
 * Regexp was taken from the android source code.
 * @author Andrea Baccega <me@andreabaccega.com>
 *
 */
public class PhoneValidator extends PatternValidator{
	public PhoneValidator(String _customErrorMessage) {

		super(_customErrorMessage, Pattern.compile("^1(3[0-9]|4[57]|5[0-35-9]|8[0-9]|70)\\d{8}$"));
	}
}
