import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { OnInit } from '@angular/core';
import { ButtonDirective } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { CommonModule } from '@angular/common';

@Component({
  imports: [RouterLink, ReactiveFormsModule, FormsModule, InputTextModule, ButtonDirective, CommonModule],
  selector: 'app-login',
  styleUrl: './login.css',
  templateUrl: './login.html',
})
export class Login {
    private readonly fb = inject(FormBuilder);
    private readonly http = inject(HttpClient);



    formLogin = this.fb.group({
        email: ['', [Validators.email, Validators.required, Validators.minLength(1), Validators.maxLength(15)]],
        password: ['', [Validators.required, Validators.minLength(1)]],
    });

    submit(){
        
    }
}
