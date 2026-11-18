import { Routes } from '@angular/router';
import { homePage } from './home-page/home-page';
import { activeSensors } from './view-active-sensors/view-active-sensors';
import { Dashboard } from './dashboard/dashboard';
import { AddNewUser } from './add-new-user/add-new-user';

export const routes: Routes = [
    { path: '', component: homePage, title: 'Home' },
    { path: 'active-sensors', component: activeSensors, title: 'Sensori-Attivi' },
    { path: 'dashboard', component: Dashboard, title: 'Dashboard' },
    { path: 'add-new-user', component: AddNewUser, title: 'add new user'}
];
