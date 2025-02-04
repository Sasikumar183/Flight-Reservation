import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-user-login',
  imports: [FormsModule,CommonModule,HttpClientModule,RouterLink],
  templateUrl: './user-login.component.html',
  styleUrl: './user-login.component.css'
})
export class UserLoginComponent {
  loginData={
    email:'',
    password:''
  }
  constructor (private http:HttpClient){}
  onSubmit() {
    let httpParams = new HttpParams()
    .set('email',this.loginData.email)
    .set('password',this.loginData.password)
    const servletUrl = 'http://localhost:8080/Flight_Backend/LoginServlet';
    this.http.post<{ id: number }>(servletUrl, httpParams, { headers: { 'Content-Type': 'application/x-www-form-urlencoded' } }).subscribe(
      (response) => {
        console.log('Login successful', response);
        alert('Login successful!');
        const userId = response.id;
        if (userId) {
          window.location.href = `/home?id=${userId}`;
        }
      },
      (error) => {
        console.error('Error during login', error);
        alert('Login failed! Please check your credentials.');
      }
    );
  }

}
