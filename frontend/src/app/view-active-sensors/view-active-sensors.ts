import { booleanAttribute, Component, inject, OnInit, signal } from '@angular/core';
import { HttpClient, HttpResponse } from "@angular/common/http";
import { Sensor } from '../interfaces/sensor.interface';
import { TableModule } from "primeng/table";




@Component({
    imports:[TableModule],
    selector: 'view-active-sensors',
    templateUrl: 'view-active-sensors.html'
})
export class activeSensors implements OnInit {
    //Istanza della classe HttpClient per effettuare le richieste al backend
    
    private readonly http = inject(HttpClient);

    //lista di sensori che sono attualmente attivi
    readonly activeSensors = signal<Sensor[]> ([
        
    ]);

    viewTable = false;

    ngOnInit(): void {
        
        this.http.get<Sensor[]>('/api/simulators', { observe: 'response' }).subscribe({
            next: (data: HttpResponse<Sensor[]>) => {
            if (!data.body || Object.keys(data.body).length === 0) {
                    this.viewTable = true
                    console.log('Il body è nullo, vuoto o contiene un oggetto/array vuoto');
                    } else {
                        this.activeSensors.set(data.body);
                        console.log("Il vettore ricevuto dall'endpoint è stato copiato dentro activeSensors:", this.activeSensors);
            }                
            },
            error:(err) => {
                alert("Errore nella richiesta");
           }
        });
    }
}
