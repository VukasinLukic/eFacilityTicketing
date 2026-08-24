import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CarUpdateModalComponent } from './car-update-modal.component';

describe('CarUpdateModalComponent', () => {
  let component: CarUpdateModalComponent;
  let fixture: ComponentFixture<CarUpdateModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CarUpdateModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CarUpdateModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
