 Election System 2026
A secure, local-area network (LAN) based voting system built with Java for school student elections.

Overview
This project is a desktop-based election management suite designed to modernize the voting process. It eliminates manual counting errors and ensures data privacy by keeping all voting records stored locally within the school's private network.

Features
Dual-Mode Architecture: Run as a Server to host the election and tally results, or as a Client to allow students to cast votes from individual terminals.

Real-time Tallying: Automatic calculation of votes with built-in protection against duplicate submissions using unique Student iD numbers.

Admin Security: Dedicated Admin panel for real-time monitoring of election results, protected by a hard-coded security key.

Data Portability: Results are automatically exported to election_results.csv, making them instantly ready for official reporting in Microsoft Excel or Google Sheets.

Getting Started
Prerequisites
Java Development Kit (JDK) 21+ installed on all participating machines.

Ensure all terminals are connected to the same local network (LAN).

Running the Application
Compile: javac *.java

Execute: java main

Setup:

Admin Machine: Select "Run as Server". Note the IP address displayed in the title bar.

Client Machine: Select "Connect as Voter" and enter the IP address displayed on the Server terminal.

Security Notice
This system uses a dedicated ElectionSystem/ folder to manage data. This folder is ignored by Git to ensure that sensitive student voting records are never uploaded to public or shared repositories.

Developed by Uranium214-AI (Kartik Maheshwari)
