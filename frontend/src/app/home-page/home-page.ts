import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ButtonDirective } from 'primeng/button';
import { ArrowCircleRight } from '@primeicons/angular/arrow-circle-right';
import { PrimeIcons } from 'primeng/api';
import { SharedModule } from 'primeng/api';


@Component({
    imports:[ButtonDirective, RouterLink, ArrowCircleRight, SharedModule],
    selector: 'home-page',
    templateUrl: 'home-page.html',
    standalone: true,
})
export class homePage {
    
}
