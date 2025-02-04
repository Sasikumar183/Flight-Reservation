import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-add-flight',
  imports: [CommonModule,FormsModule,HttpClientModule],
  templateUrl: './add-flight.component.html',
  styleUrl: './add-flight.component.css'
})
export class AddFlightComponent {
  flightId:number=0;
  flightName = '';
  flights = [{ departure: '', arrival: '', departureTime: '', arrivalTime: '', seats: 0, price: 0 ,stoppages:0}];
  
  constructor (private http:HttpClient,private router:Router){}


  addFlight() {
    this.flights.push({ departure: '', arrival: '', departureTime: '', arrivalTime: '', seats: 0, price: 0 ,stoppages:0});
    console.log(this.flights);
  }
  registerFlight() {
    if (!this.flightName || !this.flights || this.flights.length === 0) {
      alert("Please provide flight name and schedule details before submitting.");
      return;
    }

    console.log("Registering Flight:", this.flights);
    
    const URL = "http://localhost:8080/Flight_Backend/AdminFlightAdd";
    const data = { flight_name: this.flightName, schedule: this.flights };

    this.http.post<{ id: number }>(URL, data).subscribe(
      (res) => {
        this.flightId = res.id;
        alert("Your Flight has been added successfully! Flight ID: " + res.id);

        if (this.flightId) {
          if (this.router) {
            this.router.navigate(['/adminhome']);
          } else {
            window.location.href = "/adminhome";
          }
        }
      },
      (err) => {
        console.error("Flight Registration Error:", err);
        alert("Failed to add flight. Please try again later.");
      }
    );
}


  removeFlight(index: number) {
    this.flights.splice(index, 1);
  }

  formatTime(index: number, field: 'departureTime' | 'arrivalTime') {
    if (this.flights[index][field]) {
      let time = this.flights[index][field]; // e.g., "13:45"
      this.flights[index][field] = time + ":00"; // Converts to "13:45:00"
    }
  }
}
  

