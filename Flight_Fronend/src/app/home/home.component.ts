import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule, HttpParams } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  imports: [FormsModule,HttpClientModule,CommonModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit{
  flightFilter={
    source:'',
    destination:'',
    stoppages:''
  }
  selectedFlight: any;

  isModalOpen = false;
  numTickets = 1;
  userName:string=''
  userId:string=''
  Flight:any[]=[]


  constructor (private route: ActivatedRoute,private http:HttpClient,private router:Router){}

  ngOnInit(){
    const userId = this.route.snapshot.queryParamMap.get('id');
    console.log(userId);
    this.userId = userId || ''; 

    if (userId) {
      const servletUrl = `http://localhost:8080/Flight_Backend/HomeServlet?id=${userId}`;
      this.http.get<{ username: string }>(servletUrl).subscribe(
        (response) => {
          console.log(response)
          this.userName = response.username;
        },
        (error) => {
          console.error('Error fetching username', error);
        }
      );
    }
    this.fetchFlight();
  }

  fetchFlight() {
    const URL = 'http://localhost:8080/Flight_Backend/GetFlight';
    this.http.get<object[]>(URL).subscribe(
      (res) => {
        this.Flight=res;
        console.log(res)
      },
      (err) => {
        console.error('Error fetching flights:', err);
      }
    );
  }
  FilterFlight(){
    if(this.flightFilter.source!=null|| this.flightFilter.destination!=null || this.flightFilter.stoppages!=null){
    this.Flight=this.Flight.filter(
      (flight) => flight.dep_loc === this.flightFilter.source && flight.arr_loc === this.flightFilter.destination && flight.stoppages<=this.flightFilter.stoppages
    );
    const URL="http://localhost:8080/Flight_Backend/SearchFlight";
    let httpParam= new HttpParams()
    .set("source",this.flightFilter.source)
    .set("destination",this.flightFilter.destination);
    
    this.http.post(URL,httpParam, { headers: { 'Content-Type': 'application/x-www-form-urlencoded' } }).subscribe(
      (res)=>{
        console.log(this.flightFilter.source)
        console.log(this.flightFilter.destination);
        
      },
      (err)=>{
        console.log("err")
      }
    )
  }
    else{
      this.fetchFlight();
    }

  }

  navigate(){
    this.router.navigate(['/ticket'],{queryParams:{id:this.userId}});
  }
  
 


openBookingModal(flight: any) {
  this.selectedFlight = flight;
  this.isModalOpen = true;
}

closeModal() {
  this.isModalOpen = false;
  this.numTickets = 1; 
}

confirmBooking() {
  if (this.numTickets > 0 && this.numTickets <= this.selectedFlight.available_seats) {
    const data={
      flight: this.selectedFlight,
      tickets: this.numTickets,
      userId:this.userId
    };
    const URL="http://localhost:8080/Flight_Backend/BookingServlet";
    this.http.post(URL,data).subscribe(
      (res)=>{
        alert('Booking Confirmed:')
        this.closeModal(); 
        this.fetchFlight();
        this.FilterFlight();
      }
      ,(err)=>{
        console.log("Error while fetching "+err);
      }
    )
    
  } else {
    alert('Ticket full you may try other flights.');
  }
}

}
