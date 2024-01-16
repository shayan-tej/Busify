# Busify - Android Bus Ticket Booking App

Busify is an Android app developed for convenient RTC bus ticket booking, payment processing, and ticket validation. The app provides a seamless user experience for both passengers and conductors.

## Features

- **Bus Ticket Booking**: Users can search for bus routes, select their starting and destination locations, and book RTC bus tickets.

- **Dummy Payment Integration**: The app integrates with Razorpay for dummy payment processing, allowing users to simulate the payment flow.

- **Mobile Ticket Generation**: Upon successful payment, users receive a digital ticket that can be stored on their mobile devices.

- **Conductor Ticket Validation**: Conductors can use the app to scan and validate digital tickets. The app supports Bluetooth printer connectivity for ticket printing.

## Technologies Used

- **Frontend**: XML for UI layout design.
- **Backend**: Java and Kotlin for app logic.
- **Database**: Firebase Realtime Database and Firestore for real-time data storage.
- **Payment Integration**: Razorpay for dummy payment processing.

## Screenshots

![Busify](/screenshots/splash_screen.jpg)
![Passenger](/screenshots/passenger.jpg)
![Conductor](/screenshots/conductor.jpg)

## Getting Started

Follow these instructions to get a copy of the project up and running on your local machine.

### Prerequisites

- Android Studio
- Firebase account with Realtime Database and Firestore set up
- Razorpay account for payment processing

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/calus007/Busify.git
