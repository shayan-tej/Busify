package com.sip.busify;

public class Ticket {
	private String ticketNumber;
	private String dateTime;
	private String fromLocation;
	private String toLocation;
	private String busNumber;
	private String validDate;
	private String ticketPrice;

	public Ticket(String ticketNumber, String dateTime, String fromLocation, String toLocation, String busNumber, String validDate, String ticketPrice) {
		this.ticketNumber = ticketNumber;
		this.dateTime = dateTime;
		this.fromLocation = fromLocation;
		this.toLocation = toLocation;
		this.busNumber = busNumber;
		this.validDate = validDate;
		this.ticketPrice = ticketPrice;
	}

	public String getTicketNumber() {
		return ticketNumber;
	}

	public void setTicketNumber(String ticketNumber) {
		this.ticketNumber = ticketNumber;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getFromLocation() {
		return fromLocation;
	}

	public void setFromLocation(String fromLocation) {
		this.fromLocation = fromLocation;
	}

	public String getToLocation() {
		return toLocation;
	}

	public void setToLocation(String toLocation) {
		this.toLocation = toLocation;
	}

	public String getBusNumber() {
		return busNumber;
	}

	public void setBusNumber(String busNumber) {
		this.busNumber = busNumber;
	}

	public String getValidDate() {
		return validDate;
	}

	public void setValidDate(String validDate) {
		this.validDate = validDate;
	}

	public String getTicketPrice() {
		return ticketPrice;
	}

	public void setTicketPrice(String ticketPrice) {
		this.ticketPrice = ticketPrice;
	}
}
