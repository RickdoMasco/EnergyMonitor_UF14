import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { OnInit } from '@angular/core';
import { ButtonDirective } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { CommonModule } from '@angular/common';
import { homePage } from '../home-page/home-page';

@Component({
  imports: [RouterLink, ReactiveFormsModule, FormsModule, InputTextModule, ButtonDirective, CommonModule],
  selector: 'app-login',
  styleUrl: './login.css',
  templateUrl: './login.html',
  providers: [MessageService],
})
export class Login {
    private readonly fb = inject(FormBuilder);
    private readonly http = inject(HttpClient);
    private readonly router = inject(Router);
    private readonly messageService = inject(MessageService);


    formLogin = this.fb.group({
        email: ['', [Validators.email, Validators.required, Validators.minLength(1), Validators.maxLength(40)]],
        password: ['', [Validators.required, Validators.minLength(1)]],
    });

    submit(){
        this.http.post('/api/login', this.formLogin.value).subscribe({
            next: (res) => {
                this.messageService.add({
                    severity: 'success',
                    summary: 'OK',
                    detail:'Login effettuato con successo',
                    life: 6000
                });
                this.router.navigate(['home']);
            },
            error: (err) => {
                console.error(err);
            }
        });
    }
}
