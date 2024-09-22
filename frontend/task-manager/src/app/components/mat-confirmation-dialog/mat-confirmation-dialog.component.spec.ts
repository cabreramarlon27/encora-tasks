import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MatConfirmationDialogComponent } from './mat-confirmation-dialog.component';

describe('MatConfirmationDialogComponent', () => {
  let component: MatConfirmationDialogComponent;
  let fixture: ComponentFixture<MatConfirmationDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MatConfirmationDialogComponent]
    });
    fixture = TestBed.createComponent(MatConfirmationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
