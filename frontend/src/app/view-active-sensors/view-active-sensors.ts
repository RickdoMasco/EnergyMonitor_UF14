import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpClient, HttpResponse } from "@angular/common/http";
import { Sensor } from '../interfaces/sensor.interface';
import { TableModule } from "primeng/table";
import { ToastModule } from "primeng/toast";
import { MessageService } from "primeng/api";
import { Router } from '@angular/router';



@Component({
    imports:[TableModule, ToastModule],
    selector: 'view-active-sensors',
    templateUrl: 'view-active-sensors.html',
    styleUrls: ['view-active-sensors.css'],
    providers: [MessageService]
})
export class activeSensors implements OnInit {
    private readonly http = inject(HttpClient);
    private readonly messageService = inject(MessageService);
    private readonly router = inject(Router);

    readonly activeSensors = signal<Sensor[]> ([]);
    viewTable = false;

    ngOnInit(): void {
        
        this.http.get<Sensor[]>('/api/simulators', { observe: 'response' }).subscribe({
            next: (data: HttpResponse<Sensor[]>) => {
                if (!data.body || (Array.isArray(data.body) && data.body.length === 0)) {
                    this.viewTable = true;
                    this.messageService.add({
                        severity: 'warn',
                        summary: 'Attenzione',
                        detail: 'Nessun sensore attivo trovato',
                        life: 5000
                    });
                } else {
                    this.activeSensors.set(data.body);
                }
            },
            error: (err) => {
                this.messageService.add({
                    severity: 'error',
                    summary: 'Errore',
                    detail: 'Si è verificato un problema durante il recupero dei sensori attivi. Riprova più tardi.',
                    life: 5000
                });
                console.error("Errore nella richiesta:", err);
            }
        });
        const state = (this.router.getCurrentNavigation()?.extras.state || history.state) as any;
        if (state?.['success']) {
            console.log("Navigazione con successo:", state);
            setTimeout(() => {
                this.messageService.add({
                    severity: 'success',
                    summary: 'Successo',
                    detail: 'Sensore aggiunto con successo.',
                    life: 5000
                });
            });
        }
    }
}