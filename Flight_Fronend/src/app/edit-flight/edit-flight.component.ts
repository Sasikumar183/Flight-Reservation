import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-edit-flight',
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './edit-flight.component.html',
  styleUrls: ['./edit-flight.component.css']
})
export class EditFlightComponent implements OnInit {

  constructor(private http: HttpClient, private route: ActivatedRoute) { }

  ngOnInit(): void {
    const flightId = this.route.snapshot.queryParamMap.get('id');
    this.flight_id = flightId || "";
    this.fetchDetails();
  }

  Flight: string = '';
  Schedule: any = [];
  flight_id: string = '';
  fli:string=''

  fetchDetails() {
    const URL = `http://localhost:8080/Flight_Backend/AdminFlightEdit?id=${this.flight_id}`;

    this.http.get<any[]>(URL).subscribe(
      (res) => {
        if (res && res.length > 0) {
          this.Flight = res[0].flight_name;
          this.Schedule = res[1].schedule;
          console.log("Flight Data: ", this.Flight);
          console.log("Schedule Data: ", this.Schedule);
        } else {
          console.log("No flight data found.");
        }
      },
      (err) => {
        console.log("Error fetching data: ", err);
      }
    );
  }

  updateFlight() {
      const URL = `http://localhost:8080/Flight_Backend/UpdateFlight`;

      const payload = {
      flight_id: this.flight_id,
      flight_name: this.Flight,
      schedule: this.Schedule
    };

    this.http.post(URL, payload).subscribe(
      (res) => {
        console.log("Flight updated successfully:", res);
        alert("Flight updated successfully!");
        window.location.href = "/adminhome";
      },
      (err) => {
        console.log("Error updating flight:", err);
      }
    );
  }
  formatTime(index: number, type: string): void {
    let time: string = "";
    if (type === 'departureTime') {
      time = this.Schedule[index].dep_time;
    } else if (type === 'arrivalTime') {
      time = this.Schedule[index].arr_time;
    }
  
    const formattedTime = this.formatTo12HourFormat(time);
    
    if (type === 'departureTime') {
      this.Schedule[index].dep_time = formattedTime;
    } else if (type === 'arrivalTime') {
      this.Schedule[index].arr_time = formattedTime;
    }
  }
  
  formatTo12HourFormat(time: string): string {
    const [hours, minutes] = time.split(':');
    let hour = parseInt(hours);
    const period = hour >= 12 ? 'PM' : 'AM';
    
    if (hour > 12) {
      hour -= 12;
    } else if (hour === 0) {
      hour = 12;
    }
    
    return `${hour}:${minutes} ${period}`;
  }
  
}
