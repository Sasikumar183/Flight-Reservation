import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule, HttpParams } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Route } from '@angular/router';

@Component({
  selector: 'app-ticket-page',
  imports: [CommonModule,FormsModule,HttpClientModule],
  templateUrl: './ticket-page.component.html',
  styleUrl: './ticket-page.component.css'
})
export class TicketPageComponent implements OnInit{
  constructor (private http:HttpClient,private route:ActivatedRoute){
  }
  Ticket:any[]=[];
  userId:string=''

  ngOnInit(): void {
      this.fetchTickets();
  }

  fetchTickets(){
    const userId = this.route.snapshot.queryParamMap.get('id');

    const URL = `http://localhost:8080/Flight_Backend/GetTickets?id=${userId}`;
    this.http.get<object[]>(URL).subscribe(
      (res)=>{
          this.Ticket=res;
      },
      (err)=>{
        console.error('Error fetching flights:', err);

      }
    )
  }
  cancelBooking(ticket:any){
    const URL="http://localhost:8080/Flight_Backend/CancelTicket";
    const ticket_id=ticket.ticket_id;
    console.log(ticket)
    let httpParam = new HttpParams()
    .set("ticket_id",ticket_id)
    .set("sche_id",ticket.sche_id);

    let ans=confirm("Are you sure to cancel this ticket");
    if(ans){
      this.http.post(URL, httpParam, { headers: { 'Content-Type': 'application/x-www-form-urlencoded' } }).subscribe(
        (res)=>{
          console.log("Deleted");
          alert("Ticket Cancelled");
          this.fetchTickets();
        },
        (err)=>{
          console.log("Error while cancel");   
        })
     }    
  }
}
