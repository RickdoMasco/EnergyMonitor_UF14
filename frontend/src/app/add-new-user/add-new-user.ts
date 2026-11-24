import { Component, inject} from '@angular/core';
import { ButtonDirective } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { CommonModule } from '@angular/common';
import { SelectModule } from 'primeng/select';
import { Role } from '../interfaces/role.interface';
import { FormsModule } from '@angular/forms';
import { OnInit } from '@angular/core';


@Component({
  imports: [CommonModule, RouterLink, ReactiveFormsModule, ButtonDirective, InputTextModule, FormsModule, SelectModule],
  selector: 'app-add-new-user',
  styleUrl: './add-new-user.css',
  templateUrl: './add-new-user.html',
  providers: [MessageService],
})
export class AddNewUser implements OnInit{
    private readonly http = inject(HttpClient);
    private readonly fb = inject(FormBuilder);
    private readonly messageService = inject(MessageService);


    formUser = this.fb.group({
        nome: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(15)]],
        cognome: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(15)]],
        email: ['', [Validators.required, Validators.email, Validators.minLength(1), Validators.maxLength(15)]],
        password: ['', [Validators.required, Validators.minLength(1)]],
        roleId: ['', Validators.required]
    });

    //Creazione di un nuovo utente 
    submit() {
        if (this.formUser.invalid) { this.formUser.markAllAsTouched(); return; }
        this.http.post('/api/user', this.formUser.value).subscribe({
            next: () => {
                console.log("La richiesta è stata fatta");
                this.messageService.add({
                    severity: 'success',
                    summary: 'OK',
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

    Irole:Role | undefined;

    Roles: Role[] = [];
    //Prendere la lista di ruoli dal DB
    getRoles(){
        this.http.get('/api/roles').subscribe({
            next: (response: any) => {
                console.log("La richiesta per i ruoli è stata fatta");
                this.messageService.add({
                    severity: 'success',
                    summary: 'OK',
                    detail:'Ruoli recuperati con successo',
                    life: 6000
                })
                for (let i = 0; i < response.length; i++){
                    console.log("Ruolo: " + response[i].name + " con id:" + response[i].id + " aggiunto alla lista");
                    this.Roles[i] = response[i];
                }
            }
        });
    }  

    ngOnInit(): void {
        this.getRoles();
    }
    

}
