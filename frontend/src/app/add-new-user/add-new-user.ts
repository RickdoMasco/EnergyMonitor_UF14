import { Component, inject } from '@angular/core';
import { ButtonDirective } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { CommonModule } from '@angular/common';


@Component({
  imports: [CommonModule, RouterLink, ReactiveFormsModule, ButtonDirective, InputTextModule],
  selector: 'app-add-new-user',
  styleUrl: './add-new-user.css',
  templateUrl: './add-new-user.html',
  providers: [MessageService],
})
export class AddNewUser {
    private readonly http = inject(HttpClient);
    private readonly fb = inject(FormBuilder);
    private readonly messageService = inject(MessageService);


    formUser = this.fb.group({
        nome: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(15)]],
        cognome: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(15)]],
        email: ['', [Validators.required, Validators.email, Validators.minLength(1), Validators.maxLength(15)]]
    });

    submit() {
        if (this.formUser.invalid) { this.formUser.markAllAsTouched(); return; }
        this.http.post('/api/administrator', this.formUser.value).subscribe({
            next: () => {
                this.messageService.add({
                    severity: 'success',
                    summary: 'Errore',
                    detail:'Utente salvato con successo',
                    life: 6000
                })
            },
            error: (err) => {
                this.messageService.add({
                    severity: 'error',
                    summary: 'Errore',
                    detail: err?.error?.message || err?.message || 'Si è verificato un errore durante il salvataggio dell utente.',
                    life: 6000
                });
            }
        });
    }
}
