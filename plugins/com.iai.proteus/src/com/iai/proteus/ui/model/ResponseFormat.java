package com.iai.proteus.ui.model;

/**
 * Model object for response formats
 * 
 * @author Jakob Henriksson 
 *
 */
public class ResponseFormat {

	private String responseFormat;
	private boolean selected; 
	
	/**
	 * Constructor
	 * 
	 */
	public ResponseFormat(String responseFormat) {
		this.responseFormat = responseFormat;
		this.selected = false;
	}

	/**
	 * Returns the response format 
	 * 
	 * @return
	 */
	public String getResponseFormat() {
		return responseFormat;
	}

	/**
	 * Sets the response format 
	 * 
	 * @param responseFormat
	 */
	public void setResponseFormat(String responseFormat) {
		this.responseFormat = responseFormat;
	}

	/**
	 * @return the checked
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected the checked to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	/**
	 * Convenience 
	 * 
	 */
	public void select() {
		setSelected(true);
	}
	
	/**
	 * Convenience 
	 * 
	 */
	public void deselect() {
		setSelected(false);
	}

	@Override
	public String toString() {
		return responseFormat;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((responseFormat == null) ? 0 : responseFormat.hashCode());
		result = prime * result + (selected ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResponseFormat other = (ResponseFormat) obj;
		if (responseFormat == null) {
			if (other.responseFormat != null)
				return false;
		} else if (!responseFormat.equals(other.responseFormat))
			return false;
		if (selected != other.selected)
			return false;
		return true;
	}	
	
}
