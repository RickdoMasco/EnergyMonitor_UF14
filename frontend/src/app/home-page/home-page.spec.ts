import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router, provideRouter } from '@angular/router';
import { MessageService } from 'primeng/api';
import { of, throwError } from 'rxjs';
import { homePage } from './home-page';

describe('homePage', () => {
  let fixture: ComponentFixture<homePage>;
  let component: homePage;
  let http: { post: ReturnType<typeof vi.fn> };
  let messageService: MessageService;
  let addSpy: ReturnType<typeof vi.spyOn>;
  let router: { navigate: ReturnType<typeof vi.fn> };

  function createComponent(): void {
    fixture = TestBed.createComponent(homePage);
    component = fixture.componentInstance;
    addSpy = vi.spyOn(messageService, 'add');
    fixture.detectChanges();
  }

  function fillValidForm(): void {
    component.form.setValue({
      ipAddress: '192.168.1.10',
      macAddress: 'AA:BB:CC:DD:EE:FF',
    });
  }

  beforeEach(async () => {
    http = { post: vi.fn().mockReturnValue(of({ id: 1 })) };
    messageService = new MessageService();
    router = { navigate: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [homePage],
      providers: [
        provideRouter([]),
        { provide: ActivatedRoute, useValue: {} },
        { provide: HttpClient, useValue: http },
        { provide: MessageService, useValue: messageService },
        { provide: Router, useValue: router },
      ],
    })
      .overrideComponent(homePage, {
        set: { providers: [] },
      })
      .compileComponents();
  });

  it('non invia la richiesta se il form è invalido', () => {
    createComponent();

    component.submit();

    expect(http.post).not.toHaveBeenCalled();
    expect(addSpy).toHaveBeenCalledOnce();
    expect(addSpy.mock.calls[0][0].summary).toBe('Dati non validi');
  });

  it('marca come touched i campi e mostra errori required quando vuoti', () => {
    createComponent();

    component.submit();

    expect(component.form.get('ipAddress')?.hasError('required')).toBe(true);
    expect(component.form.get('macAddress')?.hasError('required')).toBe(true);
    expect(component.form.get('ipAddress')?.touched).toBe(true);
    expect(addSpy.mock.calls[0][0].detail).toContain('obbligatorio');
  });

  it('accetta IP e MAC validi e invia il payload corretto', () => {
    createComponent();
    fillValidForm();

    component.submit();

    expect(http.post).toHaveBeenCalledWith('/api/simulators', {
      ipAddress: '192.168.1.10',
      macAddress: 'AA:BB:CC:DD:EE:FF',
    });
    expect(router.navigate).toHaveBeenCalledWith(['/active-sensors'], { state: { success: true } });
  });

  it('rifiuta un IP malformato', () => {
    createComponent();
    component.form.setValue({ ipAddress: '999.1.1.1', macAddress: 'AA:BB:CC:DD:EE:FF' });

    component.submit();

    expect(component.form.get('ipAddress')?.hasError('ipAddress')).toBe(true);
    expect(http.post).not.toHaveBeenCalled();
  });

  it('rifiuta un MAC malformato', () => {
    createComponent();
    component.form.setValue({ ipAddress: '192.168.1.10', macAddress: 'AA:BB:CC:DD:EE' });

    component.submit();

    expect(component.form.get('macAddress')?.hasError('macAddress')).toBe(true);
    expect(http.post).not.toHaveBeenCalled();
  });

  it('mostra un errore specifico per un conflitto 409 del backend', () => {
    http.post.mockReturnValue(
      throwError(() => ({
        error: { errore: 'sensore già registrato con ipAddress: 192.168.1.10' },
      })),
    );
    createComponent();
    fillValidForm();

    component.submit();

    expect(addSpy).toHaveBeenCalledOnce();
    expect(addSpy.mock.calls[0][0].detail).toContain('già registrato');
  });

  it('mostra i campi non validi restituiti da un errore 422 del backend', () => {
    http.post.mockReturnValue(
      throwError(() => ({
        error: {
          errore: 'richiesta non valida: ipAddress non valido',
          campi: { ipAddress: 'ipAddress non valido' },
        },
      })),
    );
    createComponent();
    fillValidForm();

    component.submit();

    expect(addSpy.mock.calls[0][0].detail).toContain('ipAddress non valido');
  });

  it('usa un messaggio generico quando il backend non restituisce dettagli', () => {
    http.post.mockReturnValue(throwError(() => ({})));
    createComponent();
    fillValidForm();

    component.submit();

    expect(addSpy.mock.calls[0][0].detail).toContain('errore durante il salvataggio');
  });
});
