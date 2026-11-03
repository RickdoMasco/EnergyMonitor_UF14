import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ButtonDirective } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { RouterLink } from '@angular/router';

@Component({
    imports: [CommonModule, ButtonDirective, RouterLink, DialogModule, InputTextModule, ToastModule, ReactiveFormsModule],
    selector: 'home-page',
    templateUrl: 'home-page.html',
    standalone: true,
    providers: [MessageService]
})
export class homePage {
    private readonly router = inject(Router);
    private readonly http = inject(HttpClient);
    private readonly messageService = inject(MessageService);
    private readonly fb = inject(FormBuilder);

    visible = false;
    submitting = false;

    form = this.fb.group({
        ipAddress: ['', [Validators.required]],
        macAddress: ['', [Validators.required]]
    });

    show() { this.visible = true; }

    submit() {
        if (this.form.invalid) { this.form.markAllAsTouched(); return; }
        this.submitting = true;
        this.http.post('/api/simulators', this.form.value).subscribe({
            next: () => {
                this.submitting = false;
                this.visible = false;
                this.router.navigate(['/active-sensors'], { state: { success: true } });
            },
            error: (err) => {
                this.submitting = false;
                this.messageService.add({
                    severity: 'error',
                    summary: 'Errore',
                    detail: err?.error?.message || err?.message || 'Si è verificato un errore durante il salvataggio del sensore.',
                    life: 6000
                });
            }
        });
    }
}