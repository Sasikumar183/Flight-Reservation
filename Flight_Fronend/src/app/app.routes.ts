import { Routes } from '@angular/router';
import { UserSignupComponent } from './user-signup/user-signup.component';
import { UserLoginComponent } from './user-login/user-login.component';
import { HomeComponent } from './home/home.component';
import { TicketPageComponent } from './ticket-page/ticket-page.component';
import { AdminComponent } from './admin/admin.component';
import { AdminhomeComponent } from './adminhome/adminhome.component';
import { AddFlightComponent } from './add-flight/add-flight.component';
import { EditFlightComponent } from './edit-flight/edit-flight.component';

export const routes: Routes = [
    {
        path:"signup",
        component:UserSignupComponent
    },
    {
        path:"",
        component:UserLoginComponent
    },
    {
        path:'home',
        component:HomeComponent
    },
    {
        path:'ticket',
        component:TicketPageComponent
    },
    {
        path:'adminlogin',
        component:AdminComponent
    },
    {
        path:"adminhome",
        component:AdminhomeComponent
    },
    {
        path:"addflight",
        component:AddFlightComponent
    },
    {
        path:"editflight",
        component:EditFlightComponent
    }

    ];
