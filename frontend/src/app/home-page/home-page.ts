import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ButtonDirective } from 'primeng/button';


@Component({
    imports:[ButtonDirective, RouterLink],
    selector: 'home-page',
    templateUrl: 'home-page.html',
    standalone: true,
})
export class homePage {
    
}
