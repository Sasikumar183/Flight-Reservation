import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { HttpClient, HttpClientModule, HttpErrorResponse, HttpParams} from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-user-signup',
  imports: [CommonModule,FormsModule,HttpClientModule,RouterLink],
  templateUrl: './user-signup.component.html',
  styleUrl: './user-signup.component.css'
})
export class UserSignupComponent {
  signupData: any = {
    username:"",
    email:"",
    password:""
  }
  
  constructor (private http: HttpClient){}
  onSubmit(){
    let httpParam = new HttpParams()
    .set('username',this.signupData.username)
    .set('email',this.signupData.email)
    .set('password',this.signupData.password)
    const URL ='http://localhost:8080/Flight_Backend/SignupServlet';
    this.http.post<{ id: number }>(URL, httpParam, { headers: { 'Content-Type': 'application/x-www-form-urlencoded' } }).subscribe(
      (res)=>{
        console.log("Success",res);
        alert("Signup Success");
        const userId = res.id;
        if(userId){
          window.location.href='/home?id='+userId;
        }
      },
      (error:HttpErrorResponse)=>{
        if(error.status==409){
          alert("User already exist");
        }
        else{
          alert("Error during signup");
        }
      }
    )
  }
}
