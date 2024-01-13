const chai = require('chai');
const chaiHttp = require('chai-http');
const app = require('../rest'); 


chai.use(chaiHttp);
const expect = chai.expect;

describe('Search Endpoint', () => {
  it('should return an array of books when a valid search query is provided', (done) => {
    chai
      .request(app)
      .get('/search?q=example&numitems=5&page=1')
      .end((err, res) => {
        expect(res).to.have.status(200);
        expect(res.body).to.be.an('array');
        expect(res.body).to.have.lengthOf.at.most(5); 
        done();
      });
  });

  it('should return an error when an invalid search query is provided', (done) => {
    chai
      .request(app)
      .get('/search?q=invalid&numitems=5&page=1')
      .end((err, res) => {
        expect(res).to.have.status(500); 
        expect(res.body).to.have.property('error');
        done();
      });
  });


});
