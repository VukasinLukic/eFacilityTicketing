import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientUpdateModalComponent } from './client-update-modal.component';

describe('ClientUpdateModalComponent', () => {
  let component: ClientUpdateModalComponent;
  let fixture: ComponentFixture<ClientUpdateModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ClientUpdateModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClientUpdateModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
