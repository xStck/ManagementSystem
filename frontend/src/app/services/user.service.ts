import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient, HttpHeaders} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  url = environment.apiUrl;
  jsonHeader = {
    headers: new HttpHeaders().set('Content-Type', 'application/json'),
  };

  constructor(private httpClient: HttpClient) {
  }

  signup(data: any) {
    return this.httpClient.post(this.url + "/user/signup", data, this.jsonHeader)
  }

  forgotPassword(data: any) {
    return this.httpClient.post(this.url + "/user/forgotPassword", data, this.jsonHeader)
  }

  login(data: any) {
    return this.httpClient.post(this.url + "/user/login", data, this.jsonHeader)
  }

  checkToken() {
    return this.httpClient.get(this.url + "/user/checkToken");
  }

  changePassword(data: any) {
    return this.httpClient.post(this.url + "/user/changePassword", data, this.jsonHeader)
  }

  getUsers() {
    return this.httpClient.get(`${this.url}/user/get`);
  }

  update(data: any) {
    return this.httpClient.post(`${this.url}/user/update`, data, this.jsonHeader);
  }
}
