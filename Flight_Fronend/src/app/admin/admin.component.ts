import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule, HttpParams } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-admin',
  standalone: true, // ✅ Add standalone if needed
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css'] // ✅ Fix `styleUrls` (Array)
})
export class AdminComponent {
  loginData = {
    user_name: "",
    password: ""
  };

  constructor(private http: HttpClient) {}

  onSubmit() {
    const servletUrl = "http://localhost:8080/Flight_Backend/AdminLogin";

    // ✅ Fix: Convert HttpParams to string before sending
    let httpParams = new HttpParams()
      .set('user_name', this.loginData.user_name)
      .set('password', this.loginData.password)
      .toString(); // Convert to URL-encoded string

    this.http.post<{ id: number }>(servletUrl, httpParams, { 
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    }).subscribe(
      (response) => {
        console.log('Login successful', response);
        alert('Login successful!');
        if (response.id) {
          window.location.href = `/adminhome`; // ✅ Redirect to admin home
        }
      },
      (error) => {
        console.error('Error during login', error);
        alert('Login failed! Please check your credentials.');
      }
    );
  }
}
