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
import { ipAddressValidator, macAddressValidator } from './validators';

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
        ipAddress: ['', [Validators.required, ipAddressValidator()]],
        macAddress: ['', [Validators.required, macAddressValidator()]]
    });

    show() { this.visible = true; }

    submit() {
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            this.showValidationToast();
            return;
        }
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
                    detail: this.resolveBackendError(err),
                    life: 6000
                });
            }
        });
    }

    private showValidationToast(): void {
        const details: string[] = [];
        if (this.form.get('ipAddress')?.hasError('required')) {
            details.push('IP Address è obbligatorio');
        } else if (this.form.get('ipAddress')?.hasError('ipAddress')) {
            details.push('IP Address non valido (es. 192.168.1.10)');
        }
        if (this.form.get('macAddress')?.hasError('required')) {
            details.push('MAC Address è obbligatorio');
        } else if (this.form.get('macAddress')?.hasError('macAddress')) {
            details.push('MAC Address non valido (es. AA:BB:CC:DD:EE:FF)');
        }
        this.messageService.add({
            severity: 'error',
            summary: 'Dati non validi',
            detail: details.join(' · ') || 'Controlla i campi inseriti',
            life: 6000
        });
    }

    private resolveBackendError(err: unknown): string {
        const body = (err as { error?: unknown })?.error as
            | { errore?: string; message?: string; campi?: Record<string, string> }
            | string
            | undefined;
        if (typeof body === 'string' && body.length > 0) {
            return body;
        }
        if (body && typeof body === 'object') {
            const base = body.errore || body.message;
            if (body.campi) {
                const fieldErrors = Object.values(body.campi).join(' · ');
                return fieldErrors ? `${base ?? 'Dati non validi'}: ${fieldErrors}` : (base ?? 'Dati non validi');
            }
            if (base) {
                return base;
            }
        }
        return (err as { message?: string })?.message
            ?? 'Si è verificato un errore durante il salvataggio del sensore.';
    }
}