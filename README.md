# General description
This is an application for my bachelor exam developed in Java for Android mobiles that has two perspectives: one for doctors and another for patients.
Each of them can create an account and after registration they will have various functionalities specific to the type of connected user.
<br><br>The Google Firebase platform is used for data storage: 
* Authentification - for creating and managing user accounts
* Realtime Database - for data encapsulation and manipulation
* Storage - for storing files of any type
# Doctor's perspective
On doctor's page the following are available:
* a list of all patients where he/she can access personal information
* the appointments he/she will have and has had; for future appointments he/she will be able to cancel them; for the past ones, which have no status, he/she must set the status to "onorată" or "neonorată";
for appointments that are honored he/she can attach the medical prescription as a PDF file
* all the feedbacks from him/her patients and the graphic representation of the total grades (pie chart)
* a page for monthly earnings and earnings per patient as bar charts; the data can be exported as an Excel file


# Screenshots of doctor's pages
* Login and register  <br> <img src="https://user-images.githubusercontent.com/100632281/206261839-cd624b3e-dfb7-43ed-a2fa-e13b70b3c9b9.jpg" width=219px height=430px>  <img src="https://user-images.githubusercontent.com/100632281/206261972-b47fdfc9-2ab3-4232-b849-6e4aae8464f1.jpg" width=219px height=430px>

* Home <br> <img src="https://user-images.githubusercontent.com/100632281/206251662-34c23324-7681-4a4f-891a-386280b60fa4.jpg" width=219px height=430px>

* List of patients <br> <img src="https://user-images.githubusercontent.com/100632281/206256172-3fc6eb8b-2da0-44c0-b42f-c89c9d55538f.jpg" width=219px height=430px>  <img src="https://user-images.githubusercontent.com/100632281/206256445-b46d99e4-6179-4b57-8722-0a7026d632e6.jpg" width=219px height=430px>

* Appointments <br> <img src="https://user-images.githubusercontent.com/100632281/206257537-645175d1-d211-4c69-88fe-37ac8f8cddee.jpg" width=219px height=430px>  <img src="https://user-images.githubusercontent.com/100632281/206257775-542fa4cb-dff1-4798-96bc-aae8b3e99fc9.jpg" width=219px height=430px>

* Feedbacks <br> <img src="https://user-images.githubusercontent.com/100632281/206258816-78d51b64-6107-4fe1-9fbd-327a71159646.jpg" width=219px height=430px>  <img src="https://user-images.githubusercontent.com/100632281/206259658-07f0cc0c-599b-4a18-813d-d2e8dd798c1c.jpg" width=219px height=430px>

* Monthly earnings and per patient <br> <img src="https://user-images.githubusercontent.com/100632281/206260376-a471c650-495f-415b-8502-40aa0963c5d5.jpg" width=219px height=430px>  <img src="https://user-images.githubusercontent.com/100632281/206260429-a18953bc-0303-4680-844e-ee3d947c2c59.jpg" width=219px height=430px> <br> <img src="https://user-images.githubusercontent.com/100632281/206260657-f5ca017e-a632-4c70-b1e3-c77751f1cedd.jpg" width=219px height=430pxx> <img src="https://user-images.githubusercontent.com/100632281/206260810-2c8c6c8a-dbe3-4cc8-b50c-e0737f584b9f.jpg" width=219px height=430px>

* Profile (accessible from navigation menu) <br> <img src="https://user-images.githubusercontent.com/100632281/206273084-7486066b-32d9-46d4-943a-2dd535f9f0c2.jpg" width=219px height=430px>


# Patient's perspective
On patient's page the following are available:
* a list of all doctors in the clinic; for each of them is available specific information
* a list of investigations and their prices; each speciality has more investigations (e.g ophthalmology has consultation, control and other interventions)
* the appointments he/she will have and has had; for future appointments he/she will be able to cancel them; for the past ones, which has the status "onorată", he/she can give feedback (a grade from 1 to 10 and the justification of it) and also he/she can download the medical prescription
* posibility to make an appointment to any doctor and at any time depending on availability, with no necessity of additional confirmation from the clinic
* a list of all the invoices and a bar chart of monthly payments; for unpaid invoices he/she can pay online by card (online payments are integrated in the application using POST requests to the Stripe platform)
* posibility to call the call center (in Home page)
* a section for uploading personal documents as PDF file: the identity card and the health card


# Screenshots of patient's pages
* Login and register <br> <img src="https://user-images.githubusercontent.com/100632281/206262593-d575f946-00e1-4df3-9f7a-cb5dc4ff3785.jpg" width=219px height=430px>  <img src="https://user-images.githubusercontent.com/100632281/206262662-466fa26e-e52a-49be-8114-fcb99a01dcdd.jpg" width=219px height=430px>

* Home <br> <img src="https://user-images.githubusercontent.com/100632281/206263088-13a6218c-e6ff-4375-b520-2bd1472242a5.jpg" width=219px height=430px>

* List of doctors <br> <img src="https://user-images.githubusercontent.com/100632281/206263198-44d2d787-db3b-4977-99d9-b9e509c2dd66.jpg" width=219px height=430px>  <img src="https://user-images.githubusercontent.com/100632281/206263262-a09acab2-7324-4c54-bfbd-e57c2bd63a77.jpg" width=219px height=430px>

* Investigations and prices <br> <img src="https://user-images.githubusercontent.com/100632281/206263593-54a034ea-8a4e-4342-9a70-1ab4955ad683.jpg" width=219px height=430px>

* Appointments <br> <img src="https://user-images.githubusercontent.com/100632281/206263917-f1afe945-a4e1-4b49-a524-44a2416371ef.jpg" width=219px height=430px>  <img src="https://user-images.githubusercontent.com/100632281/206275167-9c4b4e52-92f2-43d5-977a-400626f0ea41.jpg" width=219px height=430px>
<br> <img src="https://user-images.githubusercontent.com/100632281/206263990-021288d4-13df-4548-a79c-025e67912aca.jpg" width=219px height=430px>  <img src="https://user-images.githubusercontent.com/100632281/206264065-64579661-e4ac-4dcc-8db2-a120cac035da.jpg" width=219px height=430px>  <img src="https://user-images.githubusercontent.com/100632281/206274553-51405061-2bf2-4177-8768-a071436f4aaf.jpg" width=219px height=430px>


* Invoices <br> <img src="https://user-images.githubusercontent.com/100632281/206264515-4806bb5e-15e1-4243-ab7f-a4882ef99c22.jpg" width=219px height=430px>  <img src="https://user-images.githubusercontent.com/100632281/206264573-12dfbcb7-91a4-4c89-86ea-7727ac0ac0f7.jpg" width=219px height=430px>

* Monthly payments <br> <img src="https://user-images.githubusercontent.com/100632281/206265058-6b1a7a6c-92cf-4264-9fec-3b7733c94a9d.jpg" width=219px height=430px>  <img src="https://user-images.githubusercontent.com/100632281/206265111-57d6946f-6030-42b1-84cd-62d6d833c974.jpg" width=219px height=430px>

* Profile (accessible from navigation menu) <br> <img src="https://user-images.githubusercontent.com/100632281/206272375-5951431f-57ff-464c-abe5-6979ad0e1dd6.jpg" width=219px height=430px>  <img src="https://user-images.githubusercontent.com/100632281/206272458-ab128509-bd71-431d-9680-4348b7022fa5.jpg" width=219px height=430px>

* Personal documents (accessible from navigation menu) <br> <img src="https://user-images.githubusercontent.com/100632281/206271873-b41da979-8cf2-445c-9d25-101a7f6f4f2e.jpg" width=219px height=430px>


# Both perspectives
Application-wide, for both type of users, the following are available:
* the profile where the personal information and the credentials can be changed and the account can be deleted
* notification page for canceled and new appointments
* a Body Mass Index calculator
* a chat where patients and doctors can communicate
* an info page about the clinic and the application


# Screenshots of pages available for both users
* Navigation menu <br> <img src="https://user-images.githubusercontent.com/100632281/206267933-56555d39-3e8d-41a3-b78b-70853d738e84.jpg" width=219px height=430px><br>
_!! Third option, "Documente personale", is available just for patient._

* BMI calculator <br> <img src="https://user-images.githubusercontent.com/100632281/206269832-4b4355bf-a737-4789-a4ed-0d63fad60745.jpg" width=219px height=430px>

* Giving a feeback for application <br> <img src="https://user-images.githubusercontent.com/100632281/206270438-c2d93139-7125-427e-a409-52f6acfa50fe.jpg" width=219px height=430px>  <img src="https://user-images.githubusercontent.com/100632281/206270919-9e333643-26ff-4e61-9511-d59caf6a18b4.jpg" width=219px height=430px>

* About us <br> <img src="https://user-images.githubusercontent.com/100632281/206271175-1f867722-3b4a-4383-b832-ed8058d3c558.jpg" width=219px height=430px>

* Notifications <br> <img src="https://user-images.githubusercontent.com/100632281/206268384-2bead4d2-4c2c-46ce-b355-0fd1e2cebb5b.jpg" width=219px height=430px>

* Chat <br> <img src="https://user-images.githubusercontent.com/100632281/206268580-897a45b3-2b29-4f02-9c42-a5cf477eb1f9.jpg" width=219px height=430px>  <img src="https://user-images.githubusercontent.com/100632281/206268623-26b19447-1244-468b-979e-64cd85c3c12b.jpg" width=219px height=430px>



