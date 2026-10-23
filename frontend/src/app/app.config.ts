import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { providePrimeNG } from 'primeng/config';
import Aura from '@primeuix/themes/aura';
import { provideHttpClient } from '@angular/common/http';

export const appConfig: ApplicationConfig = {
    providers: [
        provideBrowserGlobalErrorListeners(),
        provideRouter(routes),
        providePrimeNG({
            theme: {
                preset: Aura
            },
            license: 'eyJpZCI6IjMzMjAxNTA5LWQ1ZDgtNDg0Mi1hOWE4LWI3YjNjNjQyNGMyNCIsInByb2R1Y3QiOiJwcmltZXVpIiwidGllciI6ImNvbW11bml0eSIsInR5cGUiOiJkZXYiLCJpYXQiOjE3OTI0OTI2OTIsImV4cCI6MTgyNDAyODY5Mn0.SIlngeuO5FXft3PD4Fe35Yqzd49r31GESCQMSvX8uXlGUme-B6ms-OHqvI6V4vkSh5TST0SRrLtVOHXaw2-1CA' // <-- usa "license" invece di "licenseKey"
        }),
        provideHttpClient(),
    ]
};