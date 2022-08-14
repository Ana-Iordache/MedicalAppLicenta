# General description
This is an application for final exam (bachelor's degree) developed in Java for Android mobiles which has two perspective: one for doctors and another for patients.
Each of them can create an account and after registration they will have various functionalities specific to the type of connected user.
<br><br>The Google Firebase platform is used for data storage: 
* Authentification - for creating and managind user accounts;
* Realtime Database - for data encapsulation and manipulation
* Storage - for storing file of any type
# Doctor's perspective
On doctor's page the following are available:
* a list of all patients where he/she can access personal informations
* the appointments he/she will have and has had; for future appointments he/she will be able to cancel them; for the past ones, which doesn't have a status, he/she must set the status to "Onorată" or "Neonorată";
for appointments that are honored he/she can attach the medical prescription as a PDF file
* all the feedbacks from him/her patients and the graphic representation of the total grades
* a page for the earnings for each month as a bar chart; the data can be exported as an Excel file
# Patient's perspective
On patient's page the following are available:
* a list of all doctors in the clinic; for each are available specific informations
* a list of investigations and their prices; each speciality has more investigations (e.g ophthalmology has consultation, control and other interventions)
* the appointments he/she will have and has had; for future appointments he/she will be able to cancel them; for the past ones, if they have been honored, he/she can give feedback (a grade from 1 to 10 and the justification of it) and also he/she can download the medical prescription
* posibility to make an appointment to any doctor and at any time depending on availability, without the need for additional confimation from the clinic
* a list of all the invoices and a bar chart of monthly payments; for unpaid invoices he/she can pay online by card (online payments are simulated using POST requests to the Stripe platform)
* posibility to call the call center
* a section for uploading personal documents as PDF file: the identity card and the health card
# Both perspective
Application-wide, for both type of users, the following are available:
* the profile where the personal informations and the credentials can be changed and the account can be deleted
* notification page for canceled and new appointments
* a Body Mass Index calculator
* a chat where patients and doctor can communicate
* an info page about clinic and application
