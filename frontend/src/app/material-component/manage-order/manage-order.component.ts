import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {CategoryService} from "../../services/category.service";
import {ProductService} from "../../services/product.service";
import {BillService} from "../../services/bill.service";
import {NgxUiLoaderService} from "ngx-ui-loader";
import {SnackbarService} from "../../services/snackbar.service";
import {GlobalConstants} from "../../shared/global-constants";
import {saveAs} from "file-saver";

@Component({
  selector: 'app-manage-order',
  templateUrl: './manage-order.component.html',
  styleUrls: ['./manage-order.component.scss']
})
export class ManageOrderComponent implements OnInit {
  manageOrderForm: any = FormGroup;
  displayedColumns: string[] = [
    'name',
    'category',
    'price',
    'quantity',
    'total',
    'edit',
  ];
  dataSource: any = [];
  categories: any = [];
  products: any = [];
  price: any;
  totalAmount: number = 0.0;
  responseMessage: any;

  constructor(
    private formBuilder: FormBuilder,
    private categoryService: CategoryService,
    private productService: ProductService,
    private billService: BillService,
    private ngxService: NgxUiLoaderService,
    private snackbarService: SnackbarService
  ) {
  }

  ngOnInit(): void {
    this.ngxService.start();

    this.getCategories();

    this.manageOrderForm = this.formBuilder.group({
      name: [
        null,
        [Validators.required, Validators.pattern(GlobalConstants.nameRegex)],
      ],
      email: [
        null,
        [Validators.required, Validators.pattern(GlobalConstants.emailRegex)],
      ],
      contactNumber: [
        null,
        [
          Validators.required,
          Validators.pattern(GlobalConstants.contactNumberRegex),
        ],
      ],
      paymentMethod: [null, [Validators.required]],
      product: [null, [Validators.required]],
      category: [null, [Validators.required]],
      quantity: [null, [Validators.required]],
      price: [null, [Validators.required]],
      total: [0, [Validators.required]],
    });
  }

  getCategories() {
    this.categoryService.getFilteredCategories().subscribe(
      (response: any) => {
        this.ngxService.stop();
        this.categories = response;
      },
      (error: any) => {
        console.log(error);
        this.ngxService.stop();
        if (error.error?.message) {
          this.responseMessage = error.error?.message;
        } else {
          this.responseMessage = GlobalConstants.genericError;
        }
        this.snackbarService.openSnackBar(this.responseMessage, GlobalConstants.error);
      }
    );
  }

  getProductsByCategory(value: any) {
    this.productService.getProductsByCategory(value.id).subscribe(
      (response: any) => {
        this.products = response;
        this.manageOrderForm.controls['price'].setValue('');
        this.manageOrderForm.controls['quantity'].setValue('');
        this.manageOrderForm.controls['total'].setValue(0);
      },
      (error: any) => {
        console.log(error);
        this.ngxService.stop();
        if (error.error?.message) {
          this.responseMessage = error.error?.message;
        } else {
          this.responseMessage = GlobalConstants.genericError;
        }
        this.snackbarService.openSnackBar(this.responseMessage, GlobalConstants.error);
      }
    );
  }

  getProductDetails(value: any) {
    this.productService.getById(value.id).subscribe(
      (response: any) => {
        this.price = response.price;
        this.manageOrderForm.controls['price'].setValue(response.price);
        this.manageOrderForm.controls['quantity'].setValue(1);
        this.manageOrderForm.controls['total'].setValue(this.price * 1);
      },
      (error: any) => {
        console.log(error);
        this.ngxService.stop();
        if (error.error?.message) {
          this.responseMessage = error.error?.message;
        } else {
          this.responseMessage = GlobalConstants.genericError;
        }
        this.snackbarService.openSnackBar(this.responseMessage, GlobalConstants.error);
      }
    );
  }

  setQuantity(value: any) {
    let temp = this.manageOrderForm.controls['quantity'].value;
    if (temp > 0) {
      this.manageOrderForm.controls['total'].setValue(
        this.manageOrderForm.controls['quantity'].value *
        this.manageOrderForm.controls['price'].value
      );
    } else if (temp != '') {
      this.manageOrderForm.controls['quantity'].setValue('1');
      this.manageOrderForm.controls['total'].setValue(
        this.manageOrderForm.controls['quantity'].value *
        this.manageOrderForm.controls['price'].value
      );
    }
  }

  validateProductAdd() {
    return this.manageOrderForm.controls['total'].value === 0 ||
      this.manageOrderForm.controls['total'].value === null ||
      this.manageOrderForm.controls['quantity'].value <= 0;
  }

  validateSubmit() {
    return this.totalAmount === 0 ||
      this.manageOrderForm.controls['name'].value === null ||
      this.manageOrderForm.controls['email'].value === null ||
      this.manageOrderForm.controls['contactNumber'].value === null ||
      this.manageOrderForm.controls['paymentMethod'].value === null ||
      !this.manageOrderForm.controls['contactNumber'].valid ||
      !this.manageOrderForm.controls['email'].valid;
  }

  add() {
    let formData = this.manageOrderForm.value;
    let productName = this.dataSource.find(
      (e: { id: number; }) => e.id == formData.product.id
    );
    if (productName === undefined) {
      this.totalAmount += formData.total;
      this.dataSource.push({
        id: formData.product.id,
        name: formData.product.name,
        category: formData.category.name,
        quantity: formData.quantity,
        price: formData.price,
        total: formData.total,
      });

      this.dataSource = [...this.dataSource];
      this.snackbarService.openSnackBar(GlobalConstants.productAdded, 'success');
    } else {
      this.snackbarService.openSnackBar(
        GlobalConstants.productExistError,
        GlobalConstants.error
      );
    }
  }

  handleDeleteAction(value: any, element: any) {
    this.totalAmount -= element.total;
    this.dataSource.splice(value, 1);
    this.dataSource = [...this.dataSource];
  }

  submitAction() {
    this.ngxService.start();
    let formData = this.manageOrderForm.value;
    let data = {
      name: formData.name,
      email: formData.email,
      contactNumber: formData.contactNumber,
      paymentMethod: formData.paymentMethod,
      totalAmount: this.totalAmount.toFixed(2),
      productDetails: JSON.stringify(this.dataSource),
    };
    this.billService.generateReport(data).subscribe(
      (response: any) => {
        this.downloadFile(response?.uuid);
        this.manageOrderForm.reset();
        this.dataSource = [];
        this.totalAmount = 0;
      },
      (error: any) => {
        console.log(error)
        this.ngxService.stop();
        if (error.error?.message) {
          this.responseMessage = error.error?.message;
        } else {
          this.responseMessage = GlobalConstants.genericError;
        }
        this.snackbarService.openSnackBar(this.responseMessage, GlobalConstants.error);
      }
    );
  }

  downloadFile(fileName: any) {
    let data = {
      uuid: fileName,
    };

    this.billService.getPDF(data).subscribe((resp: any) => {
      saveAs(resp, fileName + '.pdf');
      this.ngxService.stop();
    });
  }
}
