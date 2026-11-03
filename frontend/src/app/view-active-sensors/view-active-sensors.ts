import { booleanAttribute, Component, inject, OnInit, signal } from '@angular/core';
import { HttpClient, HttpResponse } from "@angular/common/http";
import { Sensor } from '../interfaces/sensor.interface';
import { TableModule } from "primeng/table";
import { ToastModule } from "primeng/toast";
import { MessageService } from "primeng/api";



@Component({
    imports:[TableModule, ToastModule],
    selector: 'view-active-sensors',
    templateUrl: 'view-active-sensors.html',
    styleUrls: ['view-active-sensors.css'],
    providers: [MessageService]
})
export class activeSensors implements OnInit {
    //Istanza della classe HttpClient per effettuare le richieste al backend
    
    private readonly http = inject(HttpClient);
    private readonly messageService = inject(MessageService);

    //lista di sensori che sono attualmente attivi
    readonly activeSensors = signal<Sensor[]> ([
        
    ]);

    ngOnInit(): void {
        
        this.http.get<Sensor[]>('/api/simulators', { observe: 'response' }).subscribe({
            next: (data: HttpResponse<Sensor[]>) => {
            if (!data.body || (Array.isArray(data.body) && data.body.length === 0)) {
                    this.messageService.add({
                        severity: 'warn',
                        summary: 'Attenzione',
                        detail: 'Nessun sensore attivo trovato',
                        life: 5000
                    });
                    console.log('Il body è nullo, vuoto o contiene un oggetto/array vuoto');
                    } else {
                        this.activeSensors.set(data.body);
                        console.log("Il vettore ricevuto dall'endpoint è stato copiato dentro activeSensors:", this.activeSensors);
            }                
            },
            error:(err) => {
                this.messageService.add({
                    severity: 'error',
                    summary: 'Errore',
                    detail: 'Si è verificato un problema durante il recupero dei sensori attivi. Riprova più tardi.',
                    life: 5000
                });
                console.error("Errore nella richiesta:", err);
           }
        });
    }
}