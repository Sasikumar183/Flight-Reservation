import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-adminhome',
  imports: [FormsModule,CommonModule,HttpClientModule,RouterLink],
  templateUrl: './adminhome.component.html',
  styleUrl: './adminhome.component.css'
})
export class AdminhomeComponent implements OnInit  {

  constructor (private http:HttpClient){}
  
  ngOnInit(): void {
     this.fetchFlight(); 
  }
  flights:any[]=[]
  fetchFlight(){
    const URL="http://localhost:8080/Flight_Backend/FlightAdmin"
    this.http.get<object []>(URL).subscribe(
      (res)=>{
        this.flights=res;
        console.log(this.flights)
      },
      (err)=>{
        console.log("Error while fetching Data"+err);
      }
    )
  }
  editFlight(id:number){
    window.location.href = `/editflight?id=${id}`;

  }
}
