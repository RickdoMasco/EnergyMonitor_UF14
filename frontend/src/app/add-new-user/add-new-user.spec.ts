import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AddNewUser } from './add-new-user';

describe('AddNewUser', () => {
  let component: AddNewUser;
  let fixture: ComponentFixture<AddNewUser>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddNewUser],
    }).compileComponents();

    fixture = TestBed.createComponent(AddNewUser);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
