import {Component, EventEmitter, Inject, OnInit} from '@angular/core';
import {GlobalConstants} from "../../../shared/global-constants";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {SnackbarService} from "../../../services/snackbar.service";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {CategoryService} from "../../../services/category.service";

@Component({
  selector: 'app-category',
  templateUrl: './category.component.html',
  styleUrls: ['./category.component.scss']
})
export class CategoryComponent implements OnInit {
  onAddCategory = new EventEmitter();
  onEditCategory = new EventEmitter();
  onDeleteCategory = new EventEmitter();
  categoryForm: any = FormGroup;
  dialogAction: any = 'Add';
  action: any = 'Add';
  responseMessage: any;

  constructor(
    @Inject(MAT_DIALOG_DATA) public dialogData: any,
    private fb: FormBuilder,
    private categoryService: CategoryService,
    public dialogRef: MatDialogRef<CategoryComponent>,
    private snackBarService: SnackbarService
  ) {
  }

  ngOnInit(): void {
    this.categoryForm = this.fb.group({
      name: [null, [Validators.required]],
    });

    if (this.dialogData.action === 'Edit') {
      this.dialogAction = 'Edit';
      this.action = 'Update';
      this.categoryForm.patchValue(this.dialogData.data);
    }
  }

  handleSubmit() {
    if (this.dialogAction === 'Edit') {
      this.edit();
    } else {
      this.add();
    }
  }

  add() {
    let formData = this.categoryForm.value;
    let data = {
      name: formData.name,
    };

    this.categoryService.add(data).subscribe(
      (resp: any) => {
        this.dialogRef.close();
        this.onAddCategory.emit();
        this.responseMessage = resp.message;
        this.snackBarService.openSnackBar(this.responseMessage, 'success');
      },
      (error: any) => {
        this.dialogRef.close();
        console.error(error);
        if (error.error?.message) {
          this.responseMessage = error.error?.message;
        } else {
          this.responseMessage = GlobalConstants.genericError;
        }
        this.snackBarService.openSnackBar(this.responseMessage, GlobalConstants.error);
      }
    );
  }

  edit() {
    let formData = this.categoryForm.value;
    let data = {
      id: this.dialogData.data.id,
      name: formData.name,
    };

    this.categoryService.update(data).subscribe(
      (resp: any) => {
        this.dialogRef.close();
        this.onEditCategory.emit();
        this.responseMessage = resp.message;
        this.snackBarService.openSnackBar(this.responseMessage, 'success');
      },
      (error: any) => {
        this.dialogRef.close();
        if (error.error?.message) {
          this.responseMessage = error.error?.message;
        } else {
          this.responseMessage = GlobalConstants.genericError;
        }
        this.snackBarService.openSnackBar(this.responseMessage, GlobalConstants.error);
      }
    );
  }

  delete() {
    let formData = this.categoryForm.value;
    let data = {
      id: this.dialogData.data.id,
      name: formData.name,
    };

    this.categoryService.delete(data).subscribe(
      (resp: any) => {
        this.dialogRef.close();
        this.onDeleteCategory.emit();
        this.responseMessage = resp.message;
        this.snackBarService.openSnackBar(this.responseMessage, 'success');
      },
      (error: any) => {
        this.dialogRef.close();
        if (error.error?.message) {
          this.responseMessage = error.error?.message;
        } else {
          this.responseMessage = GlobalConstants.genericError;
        }
        this.snackBarService.openSnackBar(this.responseMessage, GlobalConstants.error);
      }
    );
  }

}
