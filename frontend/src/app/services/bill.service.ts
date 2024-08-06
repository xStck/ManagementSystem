import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class BillService {
  url: string = environment.apiUrl;
  jsonHeader = {
    headers: new HttpHeaders().set('Content-Type', 'application/json'),
  };

  constructor(private httpClient: HttpClient) {
  }

  generateReport(data: any) {
    return this.httpClient.post(
      `${this.url}/bill/generateReport`,
      data,
      this.jsonHeader
    );
  }

  getPDF(data: any): Observable<Blob> {
    return this.httpClient.post(`${this.url}/bill/getPdf`, data, {
      responseType: 'blob',
    });
  }

  getBills() {
    return this.httpClient.get(`${this.url}/bill/getBills`);
  }

  delete(id: any) {
    return this.httpClient.delete(`${this.url}/bill/delete/${id}`, this.jsonHeader);
  }
}
