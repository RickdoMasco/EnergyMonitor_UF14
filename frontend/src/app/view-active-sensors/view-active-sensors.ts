import { Component, inject  } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Sensor } from '../interfaces/sensor.interface';
import { TableModule } from "primeng/table";




@Component({
    imports:[TableModule],
    selector: 'view-active-sensors',
    templateUrl: 'view-active-sensors.html'
})
export class activeSensors {
    //Istanza della classe HttpClient per effettuare le richieste al backend
    http = inject(HttpClient);

    //lista di sensori che sono attualmente attivi
    activeSensors:Sensor[] = [
        {  
            id: 12345,
            ipAddress: "192.168.1.1",
            macAddress: "AB:CD:EF:10",
            functioning: true,
        },
          {  
            id: 12345,
            ipAddress: "192.168.1.2",
            macAddress: "AB:CD:EF:20",
            functioning: true,
        },
          {  
            id: 12345,
            ipAddress: "192.168.1.3",
            macAddress: "AB:CD:EF:30",
            functioning: true,
        }
    ];

    constructor() {
        this.http.get<Sensor[]>('/api/simulators').subscribe({
            next: (risposta: Sensor[]) => {
                this.activeSensors = risposta;
                console.log("Il vettore ricevuto dall'endpoint è stato copiato dentro activeSensors:", this.activeSensors);
            },
            error:(err) => {
                alert("Errore nella richiesta");
           }
        });
    }
}
