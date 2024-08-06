import {ChangeDetectorRef, Component} from '@angular/core';
import {MediaMatcher} from '@angular/cdk/layout';
import {MenuItems} from "../../../shared/menu-items";
import {jwtDecode} from "jwt-decode";

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: [],
})
export class AppSidebarComponent {
  userRole: any;
  token: any = localStorage.getItem('token');
  tokenPayload: any;

  constructor(changeDetectorRef: ChangeDetectorRef,
              media: MediaMatcher,
              public menuItems: MenuItems) {
    this.tokenPayload = jwtDecode(this.token);
    this.userRole = this.tokenPayload?.role;
  }

}
